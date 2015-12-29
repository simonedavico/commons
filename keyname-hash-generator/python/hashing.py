import hashlib

numOfCharacters = 4

def hashKey(key):
    return MD5(key)

def MD5(key):
    return hashlib.md5(key.encode('utf-8')).hexdigest()[:numOfCharacters]+"/"+key