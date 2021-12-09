#!/bin/bash
cd /etc/letsencrypt/live/yahoofantasy.warrencrasta.com
openssl pkcs12 -export -inkey privkey.pem -in fullchain.pem -out yahoo-fantasy-prod-keystore.p12 -passin pass:${YAHOO_FANTASY_KEYSTORE_PASSWORD} -passout pass:${YAHOO_FANTASY_KEYSTORE_PASSWORD}
cd /home/ubuntu/yahoo-fantasy-basketball/scripts
chmod +x stop_server.sh
chmod +x start_server.sh
./stop_server.sh
./start_server.sh