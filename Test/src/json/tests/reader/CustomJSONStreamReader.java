/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package json.tests.reader;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import java.io.IOException;
import java.io.Reader;

/**
 * A custom JSON reader, which reads only a set of elements, from a JSON stream.
 */
public class CustomJSONStreamReader extends JsonReader {
    
    // Name of the current element, if its a 'NAME' token
    private String currentElementName = null;
    
    // Value of the current element, if its a 'STRING'/'NUMBER'/'BOOLEAN' token
    private Object currentElementValue;
    
    // A stack of elements, to maintain the hierarchy of the current element.
    // Using a custom stack implementation for performance gain
    private CustomStack elementsStack = new CustomStack(10);
    
    // Elements to be retained when reading
    private String [] requiredElements;
    
    // Last token read by the reader
    private JsonToken lastToken;
    
    // Current token that this Reader points to
    private JsonToken currentToken = null;
    
    // Flag indicating the next object which follows after a BeginAObject/BeginArray token
    // is an anonymous object or not.
    private boolean isNextObjectAnonymous = true;
    
    // Temp name to assign for anonymous objects, when putting on to the elements stack
    private static final String ANONYMOUS = "ANONYMOUS";
    
    // Element separator to define the full path to any element in the json, including its hierarchy 
    private static final String SEPARATOR = "/";
    
    private boolean hasNext = true; 

    /**
     * Creates a new JSON reader that will read a JSON stream from a {@link java.io.Reader}.
     * @param in                Reader
     * @param requiredElements  Array of elements to be retained
     */
    public CustomJSONStreamReader(Reader in, String[] requiredElements) {
        super(in);
        this.requiredElements = requiredElements;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void beginArray() throws IOException {
        if (this.currentToken == JsonToken.BEGIN_ARRAY || super.peek() == JsonToken.BEGIN_ARRAY) {
            goToNextToken(false, true);
        } else {
            throw new IllegalStateException("Expected a BEGIN_ARRAY token but found " + this.currentToken);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void endArray() throws IOException {
        super.endArray();
        this.elementsStack.pop();
        this.isNextObjectAnonymous = true;
        goToNextToken(false, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void beginObject() throws IOException {
        if (this.currentToken == JsonToken.BEGIN_OBJECT || super.peek() == JsonToken.BEGIN_OBJECT) {
            goToNextToken(false, true);
        } else {
            throw new IllegalStateException("Expected a BEGIN_OBJECT token but found " + this.currentToken);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void endObject() throws IOException {
        super.endObject();
        this.elementsStack.pop();
        this.isNextObjectAnonymous = true;
        goToNextToken(false, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasNext() throws IOException {
        return this.hasNext;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonToken peek() throws IOException {
        if (this.currentToken == null) {
            return super.peek();
        } else {
            return this.currentToken;
        }
    }

    /*
     * When any next****() method is called, current tokenName/tokenValue will be returned, and
     * the pointer will move on to the next required token. Current token is returned instead of
     * the next token, because this reader is always one token ahead of the super class. Because 
     * we always have to consume the stream and read the token to check if its a required token, 
     * so that peek() would always return only the next 'required' token, but not the next available
     * token.
     */
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String nextName() throws IOException {
        if (this.currentToken == JsonToken.NAME) {
            String tempElementName = this.currentElementName;
            goToNextToken(false, false);
            return tempElementName;
        } else {
            throw new IllegalStateException("Expected a NAME token but found " + this.currentToken);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String nextString() throws IOException {
        if (this.currentToken == JsonToken.STRING || this.currentToken == JsonToken.NUMBER) {
            String tempElementValue;
            
            /*
             * {@link com.google.gson.JsonParser} reads all primitive values as String, 
             * and then convert it to the corresponding type. Following block is to support 
             * that requirement. 
             */
            if (this.currentElementValue instanceof Integer) {
                tempElementValue = Integer.toString((int) this.currentElementValue);
            } else if (this.currentElementValue instanceof Long) {
                tempElementValue = Long.toString((long) this.currentElementValue);
            } else if (this.currentElementValue instanceof Double) {
                tempElementValue = Double.toString((double) this.currentElementValue);
            } else if (this.currentElementValue instanceof Boolean) {
                tempElementValue = Boolean.toString((boolean) this.currentElementValue);
            } else {
                tempElementValue = (String) this.currentElementValue;
            }
            
            goToNextToken(false, false);
            return tempElementValue;
        } else {
            throw new IllegalStateException("Expected a STRING token but found " + this.currentToken);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean nextBoolean() throws IOException {
        if (this.currentToken == JsonToken.BOOLEAN) {
            boolean tempElementValue = (boolean) this.currentElementValue;
            goToNextToken(false, false);
            return tempElementValue;
        } else {
            throw new IllegalStateException("Expected a BOOLEAN token but found " + this.currentToken);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void nextNull() throws IOException {
        if (this.currentToken == JsonToken.NULL) {
            goToNextToken(false, false);
        } else {
            throw new IllegalStateException("Expected a NULL token but found " + this.currentToken);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double nextDouble() throws IOException {
        if (this.currentToken == JsonToken.NUMBER) {
            double tempElementValue = (double) this.currentElementValue;
            goToNextToken(false, false);
            return tempElementValue;
        } else {
            throw new IllegalStateException("Expected a NUMBER token but found " + this.currentToken);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long nextLong() throws IOException {
        if (this.currentToken == JsonToken.NUMBER) {
            long tempElementValue = (long) this.currentElementValue;
            goToNextToken(false, false);
            return tempElementValue;
        } else {
            throw new IllegalStateException("Expected a NUMBER token but found " + this.currentToken);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int nextInt() throws IOException {
        if (this.currentToken == JsonToken.NUMBER) {
            int tempElementValue = (int) this.currentElementValue;
            goToNextToken(false, false);
            return tempElementValue;
        } else {
            throw new IllegalStateException("Expected a NUMBER token but found " + this.currentToken);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        super.close();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void skipValue() throws IOException {
        goToNextToken(false, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return super.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPath() {
        return super.getPath();
    }
    
    /**
     * Move the pointer to the next required element of the json stream.
     * 
     * @param skip              Flag indicating whether the current object is a required element
     * @param isStartElement    Flag indicating whether to traverse inside the object or not. Value for this  is 
     *                          'true' when this method is called by {@link #beginObject()} or {@link #beginArray()}
     *                          methods. 
     * @throws IOException
     */
    private void goToNextToken(boolean skip, boolean isStartElement) throws IOException {
        while (super.hasNext()) {
            this.hasNext = true;
            this.lastToken = this.currentToken;
            this.currentToken = super.peek();
            switch (this.currentToken) {
                case BEGIN_OBJECT:
                    if (skip) {
                        super.skipValue();
                        popNonAnonymousObject();
                        break;
                    }
                    handleObject(skip, isStartElement);
                    return;
                case BEGIN_ARRAY:
                    if (skip) {
                        super.skipValue();
                        popNonAnonymousObject();
                        break;
                    }
                    handleArray(skip, isStartElement);
                    return;
                case NAME:
                    this.currentElementName = super.nextName();
                    skip = !isRequired(this.currentElementName);
                    
                    // Whenever a element name is read, add it to the stack.
                    this.elementsStack.push(this.currentElementName);
                    this.isNextObjectAnonymous = false;

                    if (!skip) {
                        return;
                    }
                    break;
                case NUMBER:
                    popPrimitive();
                    if (!skip) {
                        /*
                         * Number can be int/long/double. hence reading it as string, and then parsing it 
                         * to the corresponding type, during #nextString() method.
                         */
                        this.currentElementValue = super.nextString();
                        return;
                    }
                    super.skipValue();
                    break;
                case STRING:
                    popPrimitive();
                    if (!skip) {
                        this.currentElementValue = super.nextString();
                        return;
                    }
                    super.skipValue();
                    break;
                case BOOLEAN:
                    popPrimitive();
                    if (!skip) {
                        this.currentElementValue = super.nextBoolean();
                        return;
                    }
                    super.skipValue();
                    break;
                case END_DOCUMENT:
                    this.hasNext = false;
                    return;
                default:
                    super.skipValue();
            }
            isStartElement = false;
        }
        this.hasNext = false;
    }
    
    /**
     * If this is called by a beginObject/beginArray method, then traverse through the Object,
     * and go to the next required token.
     * Else, current token is the next required token, hence stop and return.
     * 
     * @param skip              Flag indicating whether the current object is a required element
     * @param isStartElement    Flag indicating whether to traverse inside the object or not
     * @throws IOException
     */
    private void handleObject(boolean skip, boolean isStartElement) throws IOException {
        if (isStartElement) {
            if (this.isNextObjectAnonymous) {
                this.elementsStack.push(ANONYMOUS);
            }
            super.beginObject();
            this.isNextObjectAnonymous = true;
            goToNextToken(skip, false);
        }
    }
    
    
    /** If this is called by a beginObject/beginArray method, then traverse through the Object,
    * and go to the next required token.
    * Else, current token is the next required token, hence stop and return.
    * 
    * @param skip              Flag indicating whether the current object is a required element
    * @param isStartElement    Flag indicating whether to traverse inside the object or not
    * @throws IOException
    */
   private void handleArray(boolean skip, boolean isStartElement) throws IOException {
       if (isStartElement) {
           if (this.isNextObjectAnonymous) {
               this.elementsStack.push(ANONYMOUS);
           }
           super.beginArray();
           this.isNextObjectAnonymous = true;
           goToNextToken(skip, false);
       }
   }
    
    /**
     *  If the current object is not an anonymous object, pop the last element from stack,
     *  which is added by the NAME block. An anonymous object is an object without a name.
     */
    private void popNonAnonymousObject() {
        // no need to empty-check
        if (!this.isNextObjectAnonymous) {
            this.elementsStack.pop();
        }
    }

    /**
     * Remove a primitive value from the current element stack.
     */
    private void popPrimitive() {
        // no need to empty-check
        if (this.lastToken == JsonToken.NAME) {
            this.elementsStack.pop();
        }
    }

    /**
     * Check whether this element is required. Return true if:
     * <ul>
     * <li>The current element is a parent of, any of the required elements.</li>
     * <li>One of the required elements, if a parent of this element.</li>
     * </ul>
     * 
     * @param element   Current element
     * @return          Flag indicating whether this element is required or not
     */
    private boolean isRequired(String element) {
        // If the required elements list is not set, consider all elements as required.
        if (this.requiredElements == null) {
            return true;
        }
        
        /*
         *  Using string-concat over string builder here, as it is better in performance for 
         *  concatenating fewer items (five or less items). Here it would concat only five or 
         *  less items (i.e: five or less named-element levels in the json) for most cases.
         */
        String elementPath = SEPARATOR;
        for (String jsonElement : this.elementsStack.toArray()) {
            if (jsonElement!= null && !jsonElement.equals(ANONYMOUS)) {
                elementPath = elementPath + SEPARATOR + jsonElement;
            }
        }
        elementPath = elementPath + SEPARATOR + element;
        
        for (String reqiredElement : this.requiredElements) {
            if (reqiredElement.startsWith(elementPath) || elementPath.startsWith(reqiredElement)) {
                return true;
            }
        }
        return false;
    }
}
