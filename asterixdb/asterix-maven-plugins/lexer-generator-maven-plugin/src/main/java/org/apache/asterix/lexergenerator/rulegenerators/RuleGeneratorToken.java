/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.asterix.lexergenerator.rulegenerators;

import org.apache.asterix.lexergenerator.LexerNode;
import org.apache.asterix.lexergenerator.rules.RulePartial;

public class RuleGeneratorToken implements RuleGenerator {

    @Override
    public LexerNode generate(String input) throws Exception {
        if (input == null || input.length() == 0)
            throw new Exception("Wrong rule format for generator token : " + input);
        LexerNode node = new LexerNode();
        node.add(new RulePartial(input));
        return node;
    }

}
