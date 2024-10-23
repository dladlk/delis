set JAVA_OPTS=-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=17777
java %JAVA_OPTS% -jar .\target\delis-validator-1.1.0.jar