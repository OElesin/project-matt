'use strict';

var AWS = require('aws-sdk');

var batch = new AWS.Batch();

var cloudformation = new AWS.Cloudformation();

// get exports
var jobQueue = ""
var jobName = ""
var jobDefinition = ""

var redisHost = "",
    redisPasswd = "",
    redisPort = ""

var esHost = "",
    esUser = "",
    esPasswd = ""

var s3Bucket = "",
    s3Prefix = ""


// job params
var params = {
    jobDefinition: jobDefinition,
    jobName: jobName,
    jobQueue: jobQueue
}


exports.handler = function(events, context) {
    console.log("Initializing lambda to call AWS Batch")

}