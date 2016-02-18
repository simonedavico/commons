package minio

import (
	"github.com/minio/minio-go"
	"os"
	"os/exec"
	"log"
	"strings"
)

func StoreOnMinio(fileName string, bucket string, key string) {
	config := minio.Config{
		AccessKeyID:     os.Getenv("MINIO_ACCESSKEYID"),
		SecretAccessKey: os.Getenv("MINIO_SECRETACCESSKEY"),
		Endpoint:        os.Getenv("MINIO_HOST"),
		}
		s3Client, err := minio.New(config)
	    if err != nil {
	        log.Fatalln(err)
	    }  
	    object, err := os.Open(fileName)
		if err != nil {
			log.Fatalln(err)
		}
		defer object.Close()
		objectInfo, err := object.Stat()
		if err != nil {
			object.Close()
			log.Fatalln(err)
		}
		err = s3Client.PutObject(bucket, key, "application/octet-stream", objectInfo.Size(), object)
		if err != nil {
			log.Fatalln(err)
		}
	}

func callMinioClient(fileName string, minioHost string, minioKey string) {
		//TODO: change, we are using sudo to elevate the priviledge in the container, but it is not nice
		//NOTE: it seems that the commands that are not in PATH, should be launched using sh -c
		log.Printf("sh -c sudo /app/mc --quiet cp " + fileName + " " + minioHost + "/runs/" + minioKey)
		cmd := exec.Command("sh", "-c", "sudo /app/mc --quiet cp " + fileName + " " + minioHost + "/runs/" + minioKey)
    	var out bytes.Buffer
		var stderr bytes.Buffer
		cmd.Stdout = &out
		cmd.Stderr = &stderr
		err := cmd.Run()
		if err != nil {
		    fmt.Println(fmt.Sprint(err) + ": " + stderr.String())
		    return
		}
		fmt.Println("Result: " + out.String())
}

func GenerateKey(fileName string) string {
	// TODO: Use the hash library to generate a hash
	trialId := os.Getenv("TRIAL_ID")
	t := strings.Split(trialId, "_")
	return ("hash/"+t[0]+"/"+t[1]+"/"+os.Getenv("CONTAINER_NAME")+"/"+os.Getenv("COLLECTOR_NAME")+"/"+os.Getenv("DATA_NAME")+"/"+fileName)
}

func GzipFile(fileName string) {
	cmd := exec.Command("gzip", fileName)
	err := cmd.Start()
	cmd.Wait()
	if err != nil {
		panic(err)
		}
}

