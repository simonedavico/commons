package kafka

import (
    "os"
    "bytes"
    "github.com/Shopify/sarama"
    "log"
    "encoding/json"
    "strings"
)

type KafkaMessage struct {
	SUT_name string `json:"SUT_name"`
	SUT_version string `json:"SUT_version"`
	Minio_key string `json:"minio_key"`
	Trial_id string `json:"trial_id"`
	Experiment_id string `json:"experiment_id"`
	Total_trials_num int `json:"total_trials_num"`
	}

func signalOnKafka(minioKey string, SUTName string, SUTVersion string) {
	totalTrials := strconv.Atoi(os.Getenv("TOTAL_TRIALS_NUM"))
	kafkaMsg := KafkaMessage{SUT_name: SUTName, SUT_version: SUTVersion, Minio_key: minioKey, Trial_id: os.Getenv("TRIAL_ID"), Experiment_id: os.Getenv("EXPERIMENT_ID"), Total_trials_num: totalTrials}
	jsMessage, err := json.Marshal(kafkaMsg)
	if err != nil {
		log.Printf("Failed to marshall json message")
		}
	//TODO: the kafka host should be passed as an environment variable
	producer, err := sarama.NewSyncProducer([]string{os.Getenv("KAFKA_HOST")+":9092"}, nil)
	if err != nil {
	    log.Fatalln(err)
	}
	defer func() {
	    if err := producer.Close(); err != nil {
	        log.Fatalln(err)
	    }
	}()
	msg := &sarama.ProducerMessage{Topic: os.Getenv("COLLECTOR_NAME"), Value: sarama.StringEncoder(jsMessage)}
	partition, offset, err := producer.SendMessage(msg)
	if err != nil {
	    log.Printf("FAILED to send message: %s\n", err)
	    } else {
	    log.Printf("> message sent to partition %d at offset %d\n", partition, offset)
	    }
	}