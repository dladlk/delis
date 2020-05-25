FROM nginx:alpine

WORKDIR /usr/share/nginx/html

COPY nginx.conf /etc/nginx/nginx.conf

COPY startup.sh /startup.sh

COPY dist/ .

RUN chown nginx -R /usr/share/nginx/html && \
    chown nginx -R /etc/nginx && \
    chmod +x /startup.sh

USER nginx

CMD [ "/startup.sh" ]
