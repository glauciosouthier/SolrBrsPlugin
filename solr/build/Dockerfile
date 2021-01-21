FROM solr:8.7


#RUN git config --global http.sslverify false

#RUN set -ex; \
#  apt-get -y update; \
#   apt-get -y install wget grep
#RUN su root mkdir -p /mycores
#RUN chown -R solr:solr /mycores
#RUN chmod -R 777 /opt/solr/server/solr/mycores

COPY --chown=solr:solr  ./scripts/ /opt/docker-solr/scripts
#RUN chmod -R 775 /opt/docker-solr/scripts

