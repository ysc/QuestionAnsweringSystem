/**
 * 
 * APDPlat - Application Product Development Platform
 * Copyright (c) 2013, 杨尚川, yang-shangchuan@qq.com
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package org.apdplat.qa.select;

import java.util.List;

import org.apdplat.qa.datasource.DataSource;
import org.apdplat.qa.datasource.FileDataSource;
import org.apdplat.qa.files.FilesConfig;
import org.apdplat.qa.model.CandidateAnswer;
import org.apdplat.qa.model.CandidateAnswerCollection;
import org.apdplat.qa.model.Evidence;
import org.apdplat.qa.model.Question;
import org.apdplat.qa.parser.WordParser;
import org.apdplat.word.recognition.PersonName;
import org.apdplat.word.segmentation.PartOfSpeech;
import org.apdplat.word.segmentation.Word;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 通用候选答案提取组件
 *
 * @author 杨尚川
 */
public class CommonCandidateAnswerSelect implements CandidateAnswerSelect {

    private static final Logger LOG = LoggerFactory.getLogger(CommonCandidateAnswerSelect.class);

    @Override
    public void select(Question question, Evidence evidence) {
        CandidateAnswerCollection candidateAnswerCollection = new CandidateAnswerCollection();

        List<Word> words = WordParser.parse(evidence.getTitle() + evidence.getSnippet());
        for (Word word : words) {
            if (word.getText().length() < 2){
                LOG.debug("忽略长度小于2的候选答案："+word);
                continue;
            }
            if(word.getPartOfSpeech().getPos().toLowerCase().startsWith(question.getQuestionType().getPos().toLowerCase())){
                CandidateAnswer answer = new CandidateAnswer();
                answer.setAnswer(word.getText());
                candidateAnswerCollection.addAnswer(answer);
                LOG.debug("成为候选答案："+word);
            }
            //处理人名
            else if(question.getQuestionType().getPos().equals("nr") && word.getPartOfSpeech()==PartOfSpeech.I){
                if(PersonName.is(word.getText())){
                    CandidateAnswer answer = new CandidateAnswer();
                    answer.setAnswer(word.getText());
                    candidateAnswerCollection.addAnswer(answer);
                    LOG.debug("成为候选答案："+word);
                }
            }
        }
        evidence.setCandidateAnswerCollection(candidateAnswerCollection);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        DataSource dataSource = new FileDataSource(FilesConfig.personNameMaterial);
        List<Question> questions = dataSource.getQuestions();

        CommonCandidateAnswerSelect commonCandidateAnswerSelect = new CommonCandidateAnswerSelect();
        int i = 1;
        for (Question question : questions) {
            LOG.info("Question " + (i++) + ": " + question.getQuestion());
            int j = 1;
            for (Evidence evidence : question.getEvidences()) {
                LOG.info("	Evidence " + j + ": ");
                LOG.info("		Title: " + evidence.getTitle());
                LOG.info("		Snippet: " + evidence.getSnippet());
                LOG.info("	Evidence " + j + " 候选答案: ");
                commonCandidateAnswerSelect.select(question, evidence);
                for (CandidateAnswer candidateAnswer : evidence.getCandidateAnswerCollection().getAllCandidateAnswer()) {
                    LOG.info("			" + candidateAnswer.getAnswer() + " : " + candidateAnswer.getScore());
                }
                j++;
                LOG.info("------------------------------------------------");
            }
            LOG.info("------------------------------------------------");
            LOG.info("");
        }
    }
}