/**
 *
 * APDPlat - Application Product Development Platform Copyright (c) 2013, 杨尚川,
 * yang-shangchuan@qq.com
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.apdplat.qa.api;

import java.util.ArrayList;
import java.util.List;
import org.apdplat.qa.datasource.BaiduDataSource;
import org.apdplat.qa.model.CandidateAnswer;
import org.apdplat.qa.model.Question;
import org.apdplat.qa.system.CommonQuestionAnsweringSystem;
import org.apdplat.qa.system.QuestionAnsweringSystem;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 将候选答案生成为json格式
 * @author 杨尚川
 */
public class JsonGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(JsonGenerator.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static String generate(CandidateAnswer candidateAnswer) {
        try {
            return MAPPER.writeValueAsString(candidateAnswer);
        } catch (Exception e) {
            LOG.error("生成候选答案的json表示出错", e);
        }
        return "{}";
    }

    public static String generate(List<CandidateAnswer> candidateAnswers) {
        return generate(candidateAnswers, -1);
    }
    public static String generate(List<CandidateAnswer> candidateAnswers, int topN) {
        if(candidateAnswers==null){
            return "[]";
        }
        if(topN > 0){
            int len = candidateAnswers.size();
            if(topN < len){
                List<CandidateAnswer> tempCandidateAnswers = new ArrayList<>(topN);
                for(int i=0; i<topN; i++){
                    tempCandidateAnswers.add(candidateAnswers.get(i));
                }
                candidateAnswers = tempCandidateAnswers;
            }
        }
        try {
            return MAPPER.writeValueAsString(candidateAnswers);
        } catch (Exception e) {
            LOG.error("生成候选答案的json表示出错", e);
        }
        return "[]";
    }

    public static void main(String[] args) {
        QuestionAnsweringSystem questionAnsweringSystem = new CommonQuestionAnsweringSystem();
        questionAnsweringSystem.setDataSource(new BaiduDataSource());
        String questionStr = "谁死后布了七十二疑冢？";
        Question question = questionAnsweringSystem.answerQuestion(questionStr);
        if (question != null) {
            List<CandidateAnswer> candidateAnswers = question.getAllCandidateAnswer();
            System.out.println(JsonGenerator.generate(candidateAnswers));
            System.out.println(JsonGenerator.generate(candidateAnswers, 1));
            System.out.println(JsonGenerator.generate(candidateAnswers, 2));
            System.out.println(JsonGenerator.generate(candidateAnswers, 9));
            System.out.println(JsonGenerator.generate(candidateAnswers, 100));
            System.out.println(JsonGenerator.generate(candidateAnswers.get(0)));
        }
    }
}
