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

/**
 *  A Custom stack implementation based on arrays. Objects are inserted and retrieved
 *  in last-in-first-out (LIFO) basis.
 * @param <T>
 */
public class ElementStack {
    private JsonElement[] stack;

    private int total = 0;

    /**
     * Create a auto-growing Stack with given initial size.
     * @param size
     */
    public ElementStack(int size) {
        stack = new JsonElement[size];
    }

    /**
     * Resize the array associated with this stack for a given size.
     * 
     * @param capacity  Size to be resized
     */
    private void resize(int capacity) {
        JsonElement[] tmp = new JsonElement[capacity];
        System.arraycopy(stack, 0, tmp, 0, total);
        stack = tmp;
    }

    /**
     * Add an element to the top of stack.
     * 
     * @param element
     *            Element to be added to the stack
     * @return Resulting stack after adding the element
     */
    public ElementStack push(JsonElement element) {
        if (stack.length == total) {
            resize(stack.length * 2);
        }
        stack[total] = element;
        total++;
        return this;
    }

    /**
     * Get and delete the top most element of the stack. If the stack is empty, it will not remove anything,
     * and will return null.
     * 
     * @return Top most element of the stack
     */
    public JsonElement pop() {
        if (!isEmpty()) {
            total--;
            JsonElement topElement = stack[total];
            stack[total] = null;
            if (total > 0 && total == stack.length / 4) {
                resize(stack.length / 2);
            }
            return topElement;
        } else {
            return null;
        }
    }
    
    /**
     * Return the top most element of the stack, without removing it. If the stack is empty, it will return null.
     * 
     * @return  Top most element of the stack
     */
    public JsonElement peek() {
        if (!isEmpty()) {
            return stack[total - 1];
        } else {
            return null;
        }
    }

    /**
     * Return the stack as an array.
     * 
     * @return  Stack as an array
     */
    public JsonElement[] toArray() {
        return this.stack;
    }
    
    /**
     * Check whether this stack is empty.
     * 
     * @return  boolean indicating whether this stack is empty
     */
    public boolean isEmpty() {
        return (total == 0);
    }
}
