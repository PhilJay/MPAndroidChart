#!/usr/bin/env bash

if [[ -z "$CRYPT_PASS" ]]
then
   read -sp 'Password: ' CRYPT_PASS
   if [[ -z "$CRYPT_PASS" ]]
   then
      echo "\$CRYPT_PASS Still empty"
      exit 1
   fi
else
   echo "\$CRYPT_PASS available"
fi

pushd signing

# to encrypt
#openssl aes-256-cbc -salt -pbkdf2 -salt -k "$CRYPT_PASS" -in secring.gpg -out secring.gpg.enc
#openssl aes-256-cbc -salt -pbkdf2 -salt -k "$CRYPT_PASS" -in gradle_secure.properties -out gradle_secure.properties.enc

# Ubuntu 18.04 (openssl 1.1.0g+) needs -md md5
# https://askubuntu.com/questions/1067762/unable-to-decrypt-text-files-with-openssl-on-ubuntu-18-04/1076708
echo "decrypt secring.gpg"
openssl aes-256-cbc -d -salt -pbkdf2 -k "$CRYPT_PASS" -in secring.gpg.enc -out secring.gpg
echo "decrypt gradle_secure.properties"
openssl aes-256-cbc -d -salt -pbkdf2 -k "$CRYPT_PASS" -in gradle_secure.properties.enc -out gradle_secure.properties

popd 1>/dev/null