package com.timestored.kdb;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;

import javax.activation.UnsupportedDataTypeException;

import kx.c;
import kx.c.Dict;
import kx.c.Flip;
import kx.c.KException;

import com.timestored.connections.ServerConfig;

/**
 * Provide a standardised connection interface to access a KDB server. 
 */
public class CConnection implements KdbConnection {

	private static final Logger LOG = Logger.getLogger(CConnection.class.getName());

	private static final int RETRIES = 1;
	
	private c c;
	private final String host;
	private final int port;
	private final String username;
	private final String password;
	// need this as c.java provides no way to know if closed.
	private boolean closed = false;

	CConnection(String host, int port, String username, 
			String password) throws KException, IOException {

		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
		c = new c(host, port, username + ":" + password);
		c.setEncoding("UTF-8");
	}
	

	CConnection(String host, int port) throws KException, IOException {
		this(host, port, null, null);
	}

	public CConnection(ServerConfig sconf) throws KException, IOException {
		this(sconf.getHost(), sconf.getPort(), sconf.getUsername(), sconf.getPassword());
	}


	/** {@inheritDoc} */ @Override
	public void close() throws IOException {
		LOG.info("close");
		closed = true;
		c.close();
	}

	/** {@inheritDoc} */ @Override
	public Flip queryFlip(String query) throws IOException, KException {
		Object k = query(query);
		if(k instanceof Flip) {
			return (Flip) k;
		}
		throw new UnsupportedDataTypeException("FlipExpected");
	}

	/** {@inheritDoc} */ @Override
	public Dict queryDict(String query) throws IOException, KException {
		Object k = query(query);
		if(k instanceof Dict) {
			return (Dict) k;
		}
		throw new UnsupportedDataTypeException("DictExpected");
	}

	/** {@inheritDoc} */ @Override
	public Object query(String query) throws IOException, KException {
		
		LOG.info("querying -> " + query);
		if(closed) {
			throw new IllegalStateException("we were closed");
		}
		
		Object ret = null;

		boolean sent = false;
		for(int r=0; !sent; r++) {
			try {
				ret = c.k(query);
				sent = true;
				LOG.fine("query queried");
			} catch (IOException e) {
				try {
					reconnect();
				} catch(IOException io) {/* do nothing */}
				if(r>=RETRIES) {
					LOG.info("giving up reconnecting");
					throw new IOException(e);
				}
			}
		}
		return ret;
	}


//	private void disconnectedTryReconnect() throws KException, IOException {
//		// reconnect and retry
//		LOG.warning(this.toString() + "-> Error querying, retrying to connect");
//		c = new c(host, port, username + ":" + password);
//		LOG.info(this.toString() + "-> reconnected");
//	}

	/** {@inheritDoc} */ @Override
	public void send(String s) throws IOException {
		sendObject(s);
	}


	private void sendObject(Object obj) throws IOException {
		LOG.info("sending -> " + obj);
		if(closed) {
			throw new IllegalStateException("we were closed");
		}

		boolean sent = false;
		for(int r=0; !sent; r++) {
			try {
				if(obj instanceof String) {
					c.ks((String)obj);	
				} else {
					c.ks(obj);
				}
				sent = true;
				LOG.info("query sent");
			} catch (IOException e) {
				try {
					LOG.info("query failed to send... reconnecting...");
					reconnect();
				} catch(IOException io) {/* do nothing */}
				if(r>=RETRIES) {
					LOG.info("giving up reconnecting");
					throw new IOException(e);
				}
			}
		}
	}

	/** {@inheritDoc} */ @Override
	public void send(Object o) throws IOException {
		sendObject(o);
	}

	public Object k() throws UnsupportedEncodingException, KException, IOException { return c.k(); }
	
	private void reconnect() throws IOException {
		if(closed) {
			throw new IllegalStateException("we were closed");
		}
		try {
			LOG.info("Trying reconnect host:" + host);
			c = new c(host, port, username + ":" + password);
		} catch (KException e) {
			throw new IOException(e);
		}
	}
	
	@Override public String getName() {
		return host + ":" + port;
	}

	@Override public boolean isConnected() {
		return !closed && c.s.isConnected();
	}

}
