<<<<<<< HEAD
stanbol-1007
============

TBD enhancement engine uses Sphinix library to convert the captured audio. Media (audio/video) data file is parsed with the ContentItem and formatted to proper audio format by Xuggler libraries. Audio speech is than extracted by Sphinix to 'plain/text' with the annotation of temporal position of the extracted text. Sphinix uses acoustic model and language model to map the utterances with the text, so the engine will also provide support of uploading acoustic model and language model.
=======
<!--
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->

# Apache Stanbol Sphinx Module

This module provides utilities for Speech TO Text Engine of Apache Stanbol.

## SphnixModelProvider service
    :::java
    @Reference
    private ModelProvider modelProvider;

The main functionality of this component is to handle the loading of Sphinx models by using the Apache Stanbol DatafileProvider infrastructure.
SphnixModelProvider provides getters for getting Acoustic, Language, Dictioanry Model for a given Speech. Loaded models loaded as InputStream are
copied to tmp folder as CMU Sphinx do not provides support for loading models as Stream, it needs file location and DatafileProvider only provides loading of models as Stream.

Loading Model in tmp folder is done for temporary basis. An issue has been created for getting details of model location. 
Stanbol-1355 https://issues.apache.org/jira/browse/STANBOL-1355 

>>>>>>> 9e56e717eb892c747b98c328c3a996f8dc16a6ae
