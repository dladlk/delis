FROM nginx:alpine

WORKDIR /usr/share/nginx/html

COPY dist/ .

COPY nginx.conf /etc/nginx/nginx.conf

ADD ./startup.sh /startup.sh

RUN ["chmod", "+x", "/startup.sh"]

CMD /startup.sh
