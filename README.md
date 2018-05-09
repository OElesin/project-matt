Project Matt: AWS S3 PII Scanner
=========================

[![Build Status](https://travis-ci.org/OElesin/project-matt.svg?branch=master)](https://travis-ci.org/OElesin/project-matt)

This project was created to help to scan your AWS S3 buckets for
PII data. The app leverages the scale and cost of AWS services ensuring
that you only pay for what you use.

When deployed, it scans your AWS S3 bucket (you can also set prefixes to
limit scan to specific paths) and detects file types automatically and
extracts possible PII using regular expressions.

The scan summary is loaded to your Elasticsearch cluster with which you can
create Kibana dashboards to report your DLP exposure.

 
### Classifiers
1. **Regex**: Currently the app detects of some key European personal data regexp patterns.
However, you can fork the project and add more regex expressions

2. **Keyword Matching**: This is currently in development. This is not released yet.
This is due large amount of domain expertise is required on this topic.

3. **Convolutional Neural Networks**: This is **active** development and will be released
with the next major update. The project will use CNN to detect sensitive or PII
words in scanned files.


### Supported File Formats
Project Matt uses Apache Tika under the hood for file parsing. Hence, all file formats 
supported out-of-the-box by Apache Tika are supported - including media files.
~~Currently, we cannot guarantee support for parsing parquet file formats. 
This is in active development and would be released within the next minor upgrades.~~

**Reading Parquet Files is now Supported** 


All compression file formats supported by Apache Tika are available.


### Deployment
An AWS Cloudformation template that deploys the jar app as an AWS Batch job
is available. [<img src="https://s3.amazonaws.com/cloudformation-examples/cloudformation-launch-stack.png" width="150"> ](https://console.aws.amazon.com/cloudformation/home?region=eu-west-1#/stacks/new?stackName=Project-Matt-S3-PII-Scan&templateURL=https://s3-eu-west-1.amazonaws.com/datafy-data-lake-public-artifacts/project-matt/cloudformation/matt-job.template.yaml). 

**NOTE: You can only scan S3 buckets in the same region as where your template
is deployed.**

#### Requirements
- Elasticsearch Cluster with HTTPS enabled: This is used to save scan reports
- Kibana: For dashboards and visualizations
- Redis: This is used to maintain the state of the application. Keeps track of last
scanned files and some other application metadata.

#### Deployment
You will need to set some environment variables which are set
via the cloudformation template. They include:
- Elasticsearch Host URL `ES_HOST` (Uses HTTPS Client)
- Elasticsearch Username `ES_USERNAME` (*if http auth is enabled*)
- Elasticsearch Username `ES_PASSWD` (*if http auth is enabled*)
- AWS S3 Bucket to scan `MY_S3_BUCKET`
- AWS S3 Prefix to scan `MY_S3_PREFIX` (**MUST BE IN THE S3 BUCKET**)
- Redis Host URL `REDIS_HOST` 
- Redis Username `REDIS_USERNAME` (*if auth is enabled*)
- Redis Username `REDIS_PASSWD`(*if auth is enabled*)

By default, maximum number of AWS S3 objects to be scanned is set to 2000. This we assume
is more ideal for performance purposes.


#### Cost
Project Matt only performs S3 GET requests, hence you pay **$0.0008** for every job execution
for S3 charges. For AWS Batch, you only pay by the instance type you select when
deploying the template. By default, the template makes use of spot instances also to save
cost.

Cost estimation does not cover supporting infrastructure such as Elasticsearch and Redis instances. 


### License
Usage is licensed under MIT License


