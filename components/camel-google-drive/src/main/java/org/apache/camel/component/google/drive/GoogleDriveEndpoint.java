/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.google.drive;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.util.component.AbstractApiEndpoint;
import org.apache.camel.util.component.ApiMethod;
import org.apache.camel.util.component.ApiMethodPropertiesHelper;
import org.apache.camel.component.google.drive.internal.GoogleDriveApiCollection;
import org.apache.camel.component.google.drive.internal.GoogleDriveApiName;
import org.apache.camel.component.google.drive.internal.GoogleDriveConstants;
import org.apache.camel.component.google.drive.internal.GoogleDrivePropertiesHelper;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

/**
 * Represents a GoogleDrive endpoint.
 */
@UriEndpoint(scheme = "google-drive", consumerClass = GoogleDriveConsumer.class, consumerPrefix = "consumer")
public class GoogleDriveEndpoint extends AbstractApiEndpoint<GoogleDriveApiName, GoogleDriveConfiguration> {
    private Object apiProxy;
    private Drive client;
    
    private GoogleDriveClientFactory clientFactory;

    private static final List<String> DEFAULT_SCOPES = Arrays.asList(DriveScopes.DRIVE_FILE, DriveScopes.DRIVE_APPS_READONLY, DriveScopes.DRIVE_METADATA_READONLY,
            DriveScopes.DRIVE);    
    
    public GoogleDriveEndpoint(String uri, GoogleDriveComponent component,
                         GoogleDriveApiName apiName, String methodName, GoogleDriveConfiguration endpointConfiguration) {
        super(uri, component, apiName, methodName, GoogleDriveApiCollection.getCollection().getHelper(apiName), endpointConfiguration);
    }

    public Producer createProducer() throws Exception {
        return new GoogleDriveProducer(this);
    }

    public Consumer createConsumer(Processor processor) throws Exception {
        // make sure inBody is not set for consumers
        if (inBody != null) {
            throw new IllegalArgumentException("Option inBody is not supported for consumer endpoint");
        }
        final GoogleDriveConsumer consumer = new GoogleDriveConsumer(this, processor);
        // also set consumer.* properties
        configureConsumer(consumer);
        return consumer;
    }

    @Override
    protected ApiMethodPropertiesHelper<GoogleDriveConfiguration> getPropertiesHelper() {
        return GoogleDrivePropertiesHelper.getHelper();
    }

    protected String getThreadProfileName() {
        return GoogleDriveConstants.THREAD_PROFILE_NAME;
    }
    
    @Override
    protected void afterConfigureProperties() {
        switch ((GoogleDriveApiName) apiName) {
            case DRIVE_FILES:
                apiProxy = getClient().files();
                break;
            case DRIVE_ABOUT:
                apiProxy = getClient().about();
                break;                
            case DRIVE_APPS:
                apiProxy = getClient().apps();
                break;         
            case DRIVE_CHANGES:
                apiProxy = getClient().changes();
                break;                
            case DRIVE_COMMENTS:
                apiProxy = getClient().comments();
                break;                
            case DRIVE_PERMISSIONS:
                apiProxy = getClient().permissions();                
                break;                    
            case DRIVE_PROPERTIES:
                apiProxy = getClient().properties();                
                break;                        
            case DRIVE_REPLIES:
                apiProxy = getClient().replies();                
                break;                    
            case DRIVE_REVISIONS:
                apiProxy = getClient().revisions();                
                break;                    
            default:
                throw new IllegalArgumentException("Invalid API name " + apiName);
        } 
    }
    
    private Drive getClient() {
        if (client == null) {
            client = getClientFactory().makeClient(configuration.getClientId(), configuration.getClientSecret(), DEFAULT_SCOPES);
        }
        return client;
    }

    @Override
    public Object getApiProxy(ApiMethod method, Map<String, Object> args) {
        return apiProxy;
    }

    public GoogleDriveClientFactory getClientFactory() {
        if (clientFactory == null) {
            clientFactory = new DefaultGoogleDriveClientFactory();
        }
        return clientFactory;
    }

    public void setClientFactory(GoogleDriveClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }
}