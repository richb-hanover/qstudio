/ Welcome to QStudio,
/ 
/ Below you will find example queries to generate each chart type:
/ 
/ - Press [Control]+[Enter] to run the single line time series charts 
/     Then on the chart tab/panel select "Time Series" chart type to draw the first example.
/ 
/ - For multi-line charts highlight the table query and press [Control]+[e] to execute the highlighted text
/     and again select the appropriate chart type in the chart panels drop down.    
/
/ If you want help there are guides located at http://www.timestored.com/qstudio/help/
/ Any feature requests etc feel free to contact us at:  contact@timestored.com


//### Example Time Series Charts
([] dt:2013.01.01+til 21; cosineWave:cos a; sineWave:sin a:0.6*til 21)

([] time:10:00t+60000*til 99; Position:0.4*a-mod[a;8]; Cost:a:100*sin 0.015*til 99)


//### Example Line / Bar / Area Chart

/ Multiple Series with Time X-Axis
([Month:2000.01m + til 12]  
	 Costs:30.0 40.0 45.0 55.0 58.0 63.0 55.0 65.0 78.0 80.0 75.0 90.0 ; 
	 Sales:10.0 12.0 14.0 18.0 26.0 42.0 74.0 90.0 110.0 130.0 155.0 167.0 )

/ Multiple Series
([] Continent:`NorthAmerica`Asia`Asia`Europe`Europe`Africa`Asia`Africa`Asia;
	 Country:`US`China`japan`Germany`UK`Zimbabwe`Bangladesh`Nigeria`Vietnam; 
	 Population:313847.0 1343239.0 127938.0 81308.0 63047.0 13010.0 152518.0 166629.0 87840.0 ;
	 GDP:15080.0 11300.0 4444.0 3114.0 2228.0 9.9 113.0 196.0 104.0 ; 
	GDPperCapita:48300.0 8400.0 34700.0 38100.0 36500.0 413.0 1788.0 732.0 3359.0 ;  
	LifeExpectancy:77.14 72.22 80.93 78.42 78.16 39.01 61.33 51.01 70.05 )


//### Bubble Chart / Scatter Plot
update GDPperCapita%20 from ([] Continent:`NorthAmerica`Asia`Asia`Europe`Europe`Africa`Asia`Africa`Asia;
	 Country:`US`China`japan`Germany`UK`Zimbabwe`Bangladesh`Nigeria`Vietnam; 
	 Population:313847.0 1343239.0 127938.0 81308.0 63047.0 13010.0 152518.0 166629.0 87840.0 ;
	 GDP:15080.0 11300.0 4444.0 3114.0 2228.0 9.9 113.0 196.0 104.0 ; 
	GDPperCapita:48300.0 8400.0 34700.0 38100.0 36500.0 413.0 1788.0 732.0 3359.0 ;  
	LifeExpectancy:77.14 72.22 80.93 78.42 78.16 39.01 61.33 51.01 70.05 )


//### Candlestick Chart
([] t:09:00t+600000*til 22; high:c+30; low:c-20; open:60+til 22; close:c:55+2*til 22; volume:22#3 9 6)


//### Heatmap
([] Continent:`NorthAmerica`Asia`Asia`Europe`Europe`Africa`Asia`Africa`Asia;
	 Country:`US`China`japan`Germany`UK`Zimbabwe`Bangladesh`Nigeria`Vietnam; 
	 Population:313847.0 1343239.0 127938.0 81308.0 63047.0 13010.0 152518.0 166629.0 87840.0 ;
	 GDP:15080.0 11300.0 4444.0 3114.0 2228.0 9.9 113.0 196.0 104.0 ; 
	GDPperCapita:48300.0 8400.0 34700.0 38100.0 36500.0 413.0 1788.0 732.0 3359.0 ;  
	LifeExpectancy:77.14 72.22 80.93 78.42 78.16 39.01 61.33 51.01 70.05 )


//### Histogram
([] Returns:cos 0.0015*til 500; Losses:cos 0.002*til 500)

//### PieChart
/ single pie
([] Country:`US`China`japan`Germany`UK`Zimbabwe`Bangladesh`Nigeria`Vietnam; 
	 GDP:15080.0 11300.0 4444.0 3114.0 2228.0 9.9 113.0 196.0 104.0 )

/ Many Pies
([] Continent:`NorthAmerica`Asia`Asia`Europe`Europe`Africa`Asia`Africa`Asia;
	 Country:`US`China`japan`Germany`UK`Zimbabwe`Bangladesh`Nigeria`Vietnam; 
	 Population:313847.0 1343239.0 127938.0 81308.0 63047.0 13010.0 152518.0 166629.0 87840.0 ;
	 GDP:15080.0 11300.0 4444.0 3114.0 2228.0 9.9 113.0 196.0 104.0 ; 
	GDPperCapita:48300.0 8400.0 34700.0 38100.0 36500.0 413.0 1788.0 732.0 3359.0 ;  
	LifeExpectancy:77.14 72.22 80.93 78.42 78.16 39.01 61.33 51.01 70.05 )



