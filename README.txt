#-------------------------------------------------------------------------------
# Copyright 2012-2013 Trento RISE
# 
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
# 
#        http://www.apache.org/licenses/LICENSE-2.0
# 
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either   express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#-------------------------------------------------------------------------------
********************
* Protocol Carrier *
********************

Use this project as library.

Create an instance of Protocol Carrier using application context and application token as arguments:
    ProtocolCarrier pc = new ProtocolCarrier(getApplicationContext(), "appToken");

Available methods (cfr. document):
    start(String appToken), stop(String appToken), clear(String appToken),
    getPendingMessages(String appToken), removeMessage(String appToken, String messageId)
