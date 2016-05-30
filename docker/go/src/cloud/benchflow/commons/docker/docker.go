package docker

import (
	"github.com/fsouza/go-dockerclient"
	"os"
	"log"
	"fmt"
)

func CreateDockerClient() docker.Client {
	endpoint := "unix:///var/run/docker.sock"
    client, err := docker.NewClient(endpoint)
	if err != nil {
		log.Fatal(err)
		}
	return *client
	}