#!/bin/sh
./keytool-importkeypair -k cl_key.jks -p cl123456 -pk8 platform.pk8 -cert platform.x509.pem -alias cl
