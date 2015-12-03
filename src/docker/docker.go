package docker

import (
	"github.com/fsouza/go-dockerclient"
	"os"
	"log"
	"fmt"
)

func createDockerClient() docker.Client {
	endpoint := "unix:///var/run/docker.sock"
    client, err := docker.NewClient(endpoint)
	if err != nil {
		log.Fatal(err)
		}
	return *client
	}

func createDockerTLSClient() docker.Client {
	path := os.Getenv("DOCKER_CERT_PATH")
	endpoint := "tcp://"+os.Getenv("DOCKER_HOST")+":"+os.Getenv("DOCKER_PORT")
    ca := fmt.Sprintf("%s/ca.pem", path)
    cert := fmt.Sprintf("%s/cert.pem", path)
    key := fmt.Sprintf("%s/key.pem", path)
    client, err := docker.NewTLSClient(endpoint, cert, key, ca)
	if err != nil {
		log.Fatal(err)
		}
	return *client
	}