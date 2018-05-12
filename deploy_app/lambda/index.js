'use strict';

var AWS = require('aws-sdk');

var batch = new AWS.Batch();

// get exports
var jobQueue = "jobQueue"
var jobName = "jobName"
var jobDefinition = "jobDefinition"


// job params
var params = {
    jobDefinition: jobDefinition,
    jobName: jobName,
    jobQueue: jobQueue
}


exports.handler = function(events, context) {
    console.log("Initializing lambda to call AWS Batch")
    batch.submitJob(params, function(err, data) {
        if (err) console.log(err, err.stack);
        else console.log(data);
    })

}