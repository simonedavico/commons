package hashing

import (
	"crypto/md5"
	"encoding/hex"
)

const numOfCharacters int = 4

func hashKey(key string) string {
	return MD5(key)
	}

func MD5(key string) string {
	hasher := md5.New()
    hasher.Write([]byte(key))
    hashString := hex.EncodeToString(hasher.Sum(nil))
	return (hashString[:numOfCharacters]+"/"+key)
	}