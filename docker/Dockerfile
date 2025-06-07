FROM tomcat:9.0

# Rimuovi la webapp di default (opzionale)
RUN rm -rf /usr/local/tomcat/webapps/ROOT

# Copia il .war dal progetto condiviso
COPY chronogram.war /usr/local/tomcat/webapps/

# Aggiungi supporto per il debug remoto
ENV JPDA_ADDRESS=0.0.0.0:8000

# Comando condizionato: run normale o debug (JPDA)
CMD ["sh", "-c", "if [ \"$DEBUG_MODE\" = \"true\" ]; then catalina.sh jpda run; else catalina.sh run; fi"]
