package com.timestored.swingxx;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

import org.jdesktop.swingx.renderer.StringValue;

import com.timestored.theme.Theme;


/**
 * Mouse adapter that provides a right click menu for a table that allows saving as various formats.
 * Also overrides the default copy behaviour to put formatted text into clipboard.
 */
public class SaveTableMouseAdapter extends MouseAdapter {

	private static final Logger LOG = Logger.getLogger(SaveTableMouseAdapter.class.getName());
	
	private final JTable table;
	private final StringValue stringValue;
	private final ImageIcon csvIcon;

	public SaveTableMouseAdapter(JTable table, ImageIcon csvIcon) {
		this(table, csvIcon, null);
	}
		
	/**
	 * @param stringValue optionally can be used to control how table values are converted to string.
	 */
	public SaveTableMouseAdapter(final JTable table, ImageIcon csvIcon, StringValue stringValue) {
		this.table = table;
		this.stringValue = stringValue;
		this.csvIcon = csvIcon;
		
		// override so that copied contents are the converted presentation values
		String property = stringValue!= null ? stringValue.toString() : null;
		table.setTransferHandler(new TransferHandler(property) {
			@Override public void exportToClipboard(JComponent comp, Clipboard clip, int action)
					throws IllegalStateException {
				
				if(action == TransferHandler.COPY) {
					boolean areaSelected = table.getSelectedRow() != -1;
					StringSelection selection = new StringSelection(TableExporter.getTable(table, stringValue, areaSelected, false, "\t"));
					clip.setContents(selection, selection);
					exportDone(comp, selection, action);
				} else {
					super.exportToClipboard(comp, clip, action);
				}
			}
		});
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		if(SwingUtilities.isRightMouseButton(e)) {
			JPopupMenu menu = new JPopupMenu();
			boolean areaSelected = table.getSelectedRow() != -1;
			
			menu.add(new CopyToClipboardAction("Copy Table", false, true));
			menu.add(new CopyToClipboardAction("Copy Selection", true, false)).setEnabled(areaSelected);
			menu.add(new CopyToClipboardAction("Copy Selection with Column Titles", true, true)).setEnabled(areaSelected);
			
			menu.add(new ExportAsCsvAction("Export Table", false, true));
			menu.add(new ExportAsCsvAction("Export Selection", true, false)).setEnabled(areaSelected);
			menu.add(new ExportAsCsvAction("Export Selection with Column Titles", true, true)).setEnabled(areaSelected);
			
			menu.add(new ExportAsXlsAction("Export XLS", false, true));
			menu.add(new ExportAsXlsAction("XLS Selection with Column Titles", true, true)).setEnabled(areaSelected);
			menu.add(new ExportAsXlsAndEmailAction("XLS Email"));
			
			menu.show(e.getComponent(), e.getX(), e.getY());
			menu.setVisible(true);
		}
		super.mouseReleased(e);
	}
	
		
	/** Allow saving table or selected cols/rows as csv */
	private class ExportAsCsvAction extends AbstractAction {

		private static final long serialVersionUID = 1L;
		private final boolean selectedAreaOnly;
		private final boolean includeHeaders;
		
		public ExportAsCsvAction(String name, boolean selectedAreaOnly, boolean includeHeaders) {
			super(name, csvIcon);
			this.selectedAreaOnly = selectedAreaOnly;
			this.includeHeaders = includeHeaders;
		}

		@Override public void actionPerformed(ActionEvent arg0) {
            try {
    			File f = File.createTempFile("document", ".csv");
        		TableExporter.saveTable(table, stringValue, selectedAreaOnly, includeHeaders, ",", f);
        		Desktop.getDesktop().open(f);
			} catch (IOException e) {
				String msg = "Error saving file: ";
				LOG.log(Level.SEVERE, msg, e);
		        JOptionPane.showMessageDialog(null, msg, "Error Saving", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private class ExportAsXlsAndEmailAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public ExportAsXlsAndEmailAction(String name) {
			super(name, Theme.CIcon.EMAIL_ATTACH.get());
		}

		@Override public void actionPerformed(ActionEvent arg0) {
			 try{
				 File f = File.createTempFile("document", ".xlsx");
				 TableExporter.generateWorkbook(table, stringValue, f, false, true);  
	     		 Desktop desktop;
	     		 if (Desktop.isDesktopSupported() 
	     			    && (desktop = Desktop.getDesktop()).isSupported(Desktop.Action.MAIL)) {

		     		 String tbl = TableExporter.getTable(table, stringValue, false, true, "\t"); // tab \t
	     			  String fp = URLEncoder.encode(f.getAbsolutePath(), StandardCharsets.UTF_8.toString());
	     			 String[] rows = tbl.split("\n");
		     		 String body = fp + "%0A%0A" + table.getRowCount() + "%20rows.%20Sample%3A";
		     		 for(int r=0; r<Math.min(3, rows.length); r++) {
		     			 body += "%0A" + URLEncoder.encode(rows[r], StandardCharsets.UTF_8.toString());
		     		 }
	     			  URI mailto = new URI("mailto:?subject=qStudio%20XLS&body=" + body);
	     			  desktop.mail(mailto);
	     			}
			 } catch (Exception e) {
				String msg = "Error saving file: ";
				LOG.log(Level.SEVERE, msg, e);
			}

		}
	}
	
	
	/** Allow saving table or selected cols/rows as xlsx */
	private class ExportAsXlsAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		private final boolean selectedAreaOnly;
		private final boolean includeHeaders;
		
		public ExportAsXlsAction(String name, boolean selectedAreaOnly, boolean includeHeaders) {
			super(name, Theme.CIcon.XLSX.get());
			this.selectedAreaOnly = selectedAreaOnly;
			this.includeHeaders = includeHeaders;
		}
		
		@Override public void actionPerformed(ActionEvent arg0) {
			 try{
			 File f = File.createTempFile("document", ".xlsx");
			 TableExporter.generateWorkbook(table, stringValue, f, selectedAreaOnly, includeHeaders);  
       		 Desktop.getDesktop().open(f);                                                // Save the workbook as myWorkbook.xlsx
			 } catch (Exception e) {
				String msg = "Error saving file: ";
				LOG.log(Level.SEVERE, msg, e);
			}

		}
	}
	
	/** Allow copying table/selection tab separated (for excel) */
	private class CopyToClipboardAction extends AbstractAction {

		private static final long serialVersionUID = 1L;
		private final boolean selectedAreaOnly;
		private final boolean includeHeaders;
		
		private CopyToClipboardAction(String name, boolean selectedAreaOnly, boolean includeHeaders) {
			super(name);
			this.selectedAreaOnly = selectedAreaOnly;
			this.includeHeaders = includeHeaders;
			
		}

		@Override public void actionPerformed(ActionEvent arg0) {
			String s = TableExporter.getTable(table, stringValue,selectedAreaOnly, includeHeaders, "\t");
			StringSelection selection = new StringSelection(s);
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
		}
	}

}