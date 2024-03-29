package com.example.demo.service;

import com.example.demo.entity.sampledata.*;
import com.example.demo.entity.sampledata.QMenteeMento;
import com.example.demo.entity.sampledata.join.Employee;
import com.example.demo.entity.sampledata.join.QAssignment;
import com.example.demo.entity.sampledata.join.QEmployee;
import com.example.demo.entity.sampledata.join.QLeisure;
import com.example.demo.jpa.repository.sampledata.CovidVaccinationCenterRepository;
import com.example.demo.jpa.repository.sampledata.EducationCostRepository;
import com.example.demo.jpa.repository.sampledata.RegionalRecoveryCostInfoRepository;
import com.example.demo.jpa.repository.sampledata.join.EmployeeRepository;
import com.example.demo.validator.SQLValidator;
import com.example.demo.vo.Question;
import com.example.demo.vo.SQLData;
import com.example.demo.vo.TestResult;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

@Service
@Transactional
public class    GradingService {

    @PersistenceContext EntityManager em;
    @Autowired SQLValidator sqlValidator;
    @Autowired TestService testService;
    @Autowired EmployeeRepository employeeRepository;
    @Autowired EducationCostRepository educationCostRepository;
    @Autowired CovidVaccinationCenterRepository covidVaccinationCenterRepository;

    @Autowired RegionalRecoveryCostInfoRepository regionalRecoveryCostInfoRepository;



    public TestResult grade(Map<String, Object> userAnswer, SQLData sqlData, BindingResult bindingResult) {
        int unit = Integer.parseInt(userAnswer.get("unit").toString());
        switch (unit) {
            case 1 : return gradeUnit1(userAnswer, sqlData, bindingResult);
            case 2 : return gradeUnit2(userAnswer, sqlData, bindingResult);
            case 3 : return gradeUnit3(userAnswer, sqlData, bindingResult);
            case 4 : return gradeUnit4(userAnswer, sqlData, bindingResult);
            case 5 : return gradeUnit5(userAnswer, sqlData, bindingResult);
            case 6 : return gradeUnit6(userAnswer, sqlData, bindingResult);
            case 7 : return gradeUnit7(userAnswer, sqlData, bindingResult);
            case 8 : return gradeUnit8(userAnswer, sqlData, bindingResult);
            case 9 : return gradeUnit9(userAnswer, sqlData, bindingResult);
            case 10 : return gradeUnit10(userAnswer, sqlData, bindingResult);
            case 11 : return gradeUnit11(userAnswer, sqlData, bindingResult);
            case 12 : return gradeUnit12(userAnswer, sqlData, bindingResult);
            case 13 : return gradeUnit13(userAnswer, sqlData, bindingResult);
            case 14 : return gradeUnit14(userAnswer, sqlData, bindingResult);
            case 15 : return gradeUnit15(userAnswer, sqlData, bindingResult);
            case 16 : return gradeUnit16(userAnswer, sqlData, bindingResult);
            case 17 : return gradeUnit17(userAnswer, sqlData, bindingResult);
        }
        return null;
    }

    public List<LinkedHashMap<String, Object>> validateAndGetSqlResult(String answer, SQLData sqlData, BindingResult bindingResult, Question question) {
        List<LinkedHashMap<String, Object>> sqlResult = null;
        sqlData.setSql(answer);
        sqlValidator.validate(sqlData, bindingResult);
        if(bindingResult.hasErrors()) question.setErrorMsg(bindingResult.getAllErrors().get(0).getCode());
        else {
            sqlResult = testService.getSQLResult(answer);
            if(sqlResult==null) question.setErrorMsg("Invaild SQL Syntax");
        }
        return sqlResult;
    }

    public List<List<String>> getSqlResultForShow(Question question, List<LinkedHashMap<String, Object>> sqlResult) {
        List<List<String>> resultForShow = null;
        if(question.getErrorMsg()==null&&sqlResult!=null&&sqlResult.size()!=0) { // "사용자에게 보여줄" 채점 데이터를 담음
            resultForShow = new ArrayList<>();
            List<String> tempRow = new ArrayList<>();
            for(Map.Entry<String, Object> entry : sqlResult.get(0).entrySet()) tempRow.add(entry.getKey());
            resultForShow.add(tempRow);
            for(LinkedHashMap<String, Object> row : sqlResult) {
                tempRow = new ArrayList<>();
                if(row != null){
                    for(Map.Entry<String, Object> entry : row.entrySet()) {
                        if(entry.getValue()==null) tempRow.add("null");
                        else tempRow.add(entry.getValue().toString());
                    }
                }
                else
                    tempRow.add("null");

                resultForShow.add(tempRow);
            }
        }
        return resultForShow;
    }

    public void showSqlResult(List<List<String>> sqlResult) {
        if(sqlResult==null) {
            System.out.println("No Data");
            return;
        }
        for(int i=0;i<sqlResult.size();i++) {
            for(int j=0;j<sqlResult.get(i).size(); j++) System.out.print(sqlResult.get(i).get(j)+" ");
            System.out.println();
        }
    }

    public TestResult gradeUnit1(Map<String, Object> userAnswer, SQLData sqlData, BindingResult bindingResult) {
        TestResult testResult = new TestResult();

        int answer1 = Integer.parseInt(userAnswer.get("question1").toString());
        int answer2 = Integer.parseInt(userAnswer.get("question2").toString());
        String answer3 = userAnswer.get("question3").toString();

        List<Question> questionList = new ArrayList<>();

        // 문제 1 채점
        Question question1 = new Question();
        question1.setNum(1);
        question1.setUserAnswer(String.valueOf(answer1));
        if(answer1==5) {
            question1.setIsCorrect(true);
            testResult.setCorrectCount(testResult.getCorrectCount()+1);
        }


        // 문제 2 채점
        Question question2 = new Question();
        question2.setNum(2);
        question2.setUserAnswer(String.valueOf(answer2));
        if(answer2==2) {
            question2.setIsCorrect(true);
            testResult.setCorrectCount(testResult.getCorrectCount()+1);
        }


        // 문제 3 채점
        Question question3 = new Question();
        question3.setNum(3);
        question3.setUserAnswer(answer3);

        List<LinkedHashMap<String, Object>> sqlResult = validateAndGetSqlResult(answer3, sqlData, bindingResult, question3); // 사용자의 답안을 검증하고 sql 결과를 가져온다.
        question3.setSqlResult(getSqlResultForShow(question3, sqlResult)); // 사용자에게 보여줄 sql 결과를 List<List<String>> 타입으로 생성 후 저장
        // showSqlResult(question3.getSqlResult()); // 유저가 생성한 sql 결과를 확인

        List<Employee> employeeList = employeeRepository.findAll(); // 답안
        int correctCount=0;
        if(sqlResult!=null&&sqlResult.size()==5&&sqlResult.get(0).size()==3) {
            for(int i=0;i<5;i++) {
                if(sqlResult.get(i).containsKey("NAME")&&sqlResult.get(i).get("NAME").toString().equals(employeeList.get(i).getName())) correctCount++;
                if(sqlResult.get(i).containsKey("POSITION")&&sqlResult.get(i).get("POSITION").toString().equals(employeeList.get(i).getPosition())) correctCount++;
                if(sqlResult.get(i).containsKey("SALARY")&&sqlResult.get(i).get("SALARY").toString().equals(String.valueOf(employeeList.get(i).getSalary()))) correctCount++;
            }
        }
        if(correctCount==15) {
            question3.setIsCorrect(true);
            testResult.setCorrectCount(testResult.getCorrectCount()+1);
        }

        questionList.add(question1);
        questionList.add(question2);
        questionList.add(question3);
        testResult.setQuestionList(questionList);
        return testResult;
    }

    public TestResult gradeUnit2(Map<String, Object> userAnswer, SQLData sqlData, BindingResult bindingResult) {
        TestResult testResult = new TestResult();

        int answer1 = Integer.parseInt(userAnswer.get("question1").toString());
        int answer2 = Integer.parseInt(userAnswer.get("question2").toString());

        List<Question> questionList = new ArrayList<>();

        // 문제 1 채점
        Question question1 = new Question();
        question1.setNum(1);
        question1.setUserAnswer(String.valueOf(answer1));
        if(answer1==4) {
            question1.setIsCorrect(true);
            testResult.setCorrectCount(testResult.getCorrectCount()+1);
        }


        // 문제 2 채점
        Question question2 = new Question();
        question2.setNum(2);
        question2.setUserAnswer(String.valueOf(answer2));
        if(answer2==3) {
            question2.setIsCorrect(true);
            testResult.setCorrectCount(testResult.getCorrectCount()+1);
        }

        questionList.add(question1);
        questionList.add(question2);
        testResult.setQuestionList(questionList);
        return testResult;
    }

    public TestResult gradeUnit3(Map<String, Object> userAnswer, SQLData sqlData, BindingResult bindingResult) {
        TestResult testResult = new TestResult();

        List<Question> questionList = new ArrayList<>();


        int answer1 = Integer.parseInt(userAnswer.get("question1").toString());
        int answer2 = Integer.parseInt(userAnswer.get("question2").toString());

        // 문제 1 채점
        Question question1 = new Question();
        question1.setNum(1);
        question1.setUserAnswer(String.valueOf(answer1));
        if(answer1==4) {
            question1.setIsCorrect(true);
            testResult.setCorrectCount(testResult.getCorrectCount()+1);
        }


        // 문제 2 채점
        Question question2 = new Question();
        question2.setNum(2);
        question2.setUserAnswer(String.valueOf(answer2));
        if(answer2==3) {
            question2.setIsCorrect(true);
            testResult.setCorrectCount(testResult.getCorrectCount()+1);
        }


        questionList.add(question1);
        questionList.add(question2);
        testResult.setQuestionList(questionList);
        return testResult;
    }

    public TestResult gradeUnit4(Map<String, Object> userAnswer, SQLData sqlData, BindingResult bindingResult) {
        TestResult testResult = new TestResult();

        int answer1 = Integer.parseInt(userAnswer.get("question1").toString());
        int answer2 = Integer.parseInt(userAnswer.get("question2").toString());

        List<Question> questionList = new ArrayList<>();

        // 문제 1 채점
        Question question1 = new Question();
        question1.setNum(1);
        question1.setUserAnswer(String.valueOf(answer1));
        if(answer1==2) {
            question1.setIsCorrect(true);
            testResult.setCorrectCount(testResult.getCorrectCount()+1);
        }


        // 문제 2 채점
        Question question2 = new Question();
        question2.setNum(2);
        question2.setUserAnswer(String.valueOf(answer2));
        if(answer2==5) {
            question2.setIsCorrect(true);
            testResult.setCorrectCount(testResult.getCorrectCount()+1);
        }

        questionList.add(question1);
        questionList.add(question2);
        testResult.setQuestionList(questionList);
        return testResult;
    }

    public TestResult gradeUnit5(Map<String, Object> userAnswer, SQLData sqlData, BindingResult bindingResult) {
        TestResult testResult = new TestResult();

        int answer1 = Integer.parseInt(userAnswer.get("question1").toString());
        int answer2 = Integer.parseInt(userAnswer.get("question2").toString());
        String answer3 = userAnswer.get("question3").toString();


        List<Question> questionList = new ArrayList<>();

        // 문제 1 채점
        Question question1 = new Question();
        question1.setNum(1);
        question1.setUserAnswer(String.valueOf(answer1));
        if(answer1==2) {
            question1.setIsCorrect(true);
            testResult.setCorrectCount(testResult.getCorrectCount()+1);
        }

        // 문제 2 채점
        Question question2 = new Question();
        question2.setNum(2);
        question2.setUserAnswer(String.valueOf(answer2));
        if(answer2==4) {
            question2.setIsCorrect(true);
            testResult.setCorrectCount(testResult.getCorrectCount()+1);
        }


        // 문제 3 채점
        Question question3 = new Question();
        question3.setNum(3);
        question3.setUserAnswer(answer3);

        List<LinkedHashMap<String, Object>> sqlResult = validateAndGetSqlResult(answer3, sqlData, bindingResult, question3); // 사용자의 답안을 검증하고 sql 결과를 가져온다.
        question3.setSqlResult(getSqlResultForShow(question3, sqlResult)); // 사용자에게 보여줄 sql 결과를 List<List<String>> 타입으로 생성 후 저장
        //showSqlResult(question3.getSqlResult()); // 유저가 생성한 sql 결과를 확인

        if(!answer3.toUpperCase().contains("REPLACE")) question3.setErrorMsg("You should contain 'REPLACE' method!");
        if(sqlResult!=null&&sqlResult.size()==1&&sqlResult.get(0).containsKey("'MySQL'")&&sqlResult.get(0).get("'MySQL'").toString().equals("MySQL")) {
            question3.setIsCorrect(true);
            testResult.setCorrectCount(testResult.getCorrectCount()+1);
        }

        questionList.add(question1);
        questionList.add(question2);
        questionList.add(question3);
        testResult.setQuestionList(questionList);
        return testResult;
    }

    public TestResult gradeUnit6(Map<String, Object> userAnswer, SQLData sqlData, BindingResult bindingResult) {
        TestResult testResult = new TestResult();

        int answer1 = Integer.parseInt(userAnswer.get("question1").toString());
        int answer2 = Integer.parseInt(userAnswer.get("question2").toString());
        int answer3 = Integer.parseInt(userAnswer.get("question3").toString());

        List<Question> questionList = new ArrayList<>();

        // 문제 1 채점
        Question question1 = new Question();
        question1.setNum(1);

        question1.setUserAnswer(String.valueOf(answer1));
        if(answer1==5) {
            question1.setIsCorrect(true);
            testResult.setCorrectCount(testResult.getCorrectCount()+1);
        }

        // 문제 2 채점
        Question question2 = new Question();
        question2.setNum(2);

        question2.setUserAnswer(String.valueOf(answer2));
        if(answer2==4) {
            question2.setIsCorrect(true);
            testResult.setCorrectCount(testResult.getCorrectCount()+1);
        }


        // 문제 3 채점
        Question question3 = new Question();
        question3.setNum(3);

        question3.setUserAnswer(String.valueOf(answer3));
        if(answer3==3) {
            question3.setIsCorrect(true);
            testResult.setCorrectCount(testResult.getCorrectCount()+1);
        }

        questionList.add(question1);
        questionList.add(question2);
        questionList.add(question3);
        testResult.setQuestionList(questionList);
        return testResult;
    }

    public TestResult gradeUnit7(Map<String, Object> userAnswer, SQLData sqlData, BindingResult bindingResult) {
        TestResult testResult = new TestResult();

        int answer1 = Integer.parseInt(userAnswer.get("question1").toString());
        int answer2 = Integer.parseInt(userAnswer.get("question2").toString());
        String answer3 = userAnswer.get("question3").toString();

        List<Question> questionList = new ArrayList<>();

        // 문제 1 채점
        Question question1 = new Question();
        question1.setNum(1);
        question1.setUserAnswer(String.valueOf(answer1));
        if(answer1==4) {
            question1.setIsCorrect(true);
            testResult.setCorrectCount(testResult.getCorrectCount()+1);
        }

        // 문제 2 채점
        Question question2 = new Question();
        question2.setNum(2);
        question2.setUserAnswer(String.valueOf(answer2));
        if(answer2==2) {
            question2.setIsCorrect(true);
            testResult.setCorrectCount(testResult.getCorrectCount()+1);
        }

        // 문제 3 채점
        Question question3 = new Question();
        question3.setNum(3);
        question3.setUserAnswer(answer3);

        List<LinkedHashMap<String, Object>> sqlResult = validateAndGetSqlResult(answer3, sqlData, bindingResult, question3); // 사용자의 답안을 검증하고 sql 결과를 가져온다.
        question3.setSqlResult(getSqlResultForShow(question3, sqlResult)); // 사용자에게 보여줄 sql 결과를 List<List<String>> 타입으로 생성 후 저장
        showSqlResult(question3.getSqlResult()); // 유저가 생성한 sql 결과를 확인

        // 답 : SELECT DAYNAME('2023-01-01');

        if(sqlResult!=null&&sqlResult.size()==1&&sqlResult.get(0).containsKey("'Sunday'")&&sqlResult.get(0).get("'Sunday'").toString().equals("Sunday")) {
            question3.setIsCorrect(true);
            testResult.setCorrectCount(testResult.getCorrectCount()+1);
        }

        questionList.add(question1);
        questionList.add(question2);
        questionList.add(question3);
        testResult.setQuestionList(questionList);
        return testResult;
    }

    public TestResult gradeUnit8(Map<String, Object> userAnswer, SQLData sqlData, BindingResult bindingResult) {
        TestResult testResult = new TestResult();

        int answer1 = Integer.parseInt(userAnswer.get("question1").toString());
        int answer2 = Integer.parseInt(userAnswer.get("question2").toString());
        String answer3 = userAnswer.get("question3").toString();

        List<Question> questionList = new ArrayList<>();

        // 문제 1 채점
        Question question1 = new Question();
        question1.setNum(1);
        question1.setUserAnswer(String.valueOf(answer1));
        if(answer1==2) {
            question1.setIsCorrect(true);
            testResult.setCorrectCount(testResult.getCorrectCount()+1);
        }

        // 문제 2 채점
        Question question2 = new Question();
        question2.setNum(2);
        question2.setUserAnswer(String.valueOf(answer2));
        if(answer2==4) {
            question2.setIsCorrect(true);
            testResult.setCorrectCount(testResult.getCorrectCount()+1);
        }

        // 문제 3 채점
        Question question3 = new Question();
        question3.setNum(3);
        question3.setUserAnswer(answer3);

        List<LinkedHashMap<String, Object>> sqlResult = validateAndGetSqlResult(answer3, sqlData, bindingResult, question3); // 사용자의 답안을 검증하고 sql 결과를 가져온다.
        question3.setSqlResult(getSqlResultForShow(question3, sqlResult)); // 사용자에게 보여줄 sql 결과를 List<List<String>> 타입으로 생성 후 저장
        //showSqlResult(question3.getSqlResult()); // 유저가 생성한 sql 결과를 확인

        // SELECT AVG(AGE) FROM EMPLOYEE;
        JPAQueryFactory query = new JPAQueryFactory(em);
        QEmployee qe = QEmployee.employee;
        List<Double> resultList3 =
                query.select(qe.age.avg())
                        .from(qe)
                        .fetch();
        //System.out.println(resultList3.get(0));

        if(sqlResult!=null&&sqlResult.size()==1&&sqlResult.get(0).containsKey("AVG(AGE)")&&Double.parseDouble(String.valueOf(sqlResult.get(0).get("AVG(AGE)")))==resultList3.get(0)) {
            question3.setIsCorrect(true);
            testResult.setCorrectCount(testResult.getCorrectCount()+1);
        }

        questionList.add(question1);
        questionList.add(question2);
        questionList.add(question3);
        testResult.setQuestionList(questionList);
        return testResult;
    }

    public TestResult gradeUnit9(Map<String, Object> userAnswer, SQLData sqlData, BindingResult bindingResult) {
        TestResult testResult = new TestResult();

        List<Question> questionList = new ArrayList<>();

        int answer1 = Integer.parseInt(userAnswer.get("question1").toString());
        int answer2 = Integer.parseInt(userAnswer.get("question2").toString());
        String answer3 = userAnswer.get("question3").toString();

        // 문제 1 채점

        Question question1 = new Question();
        question1.setNum(1);
        question1.setUserAnswer(String.valueOf(answer1));
        if(answer1==2) {
            question1.setIsCorrect(true);
            testResult.setCorrectCount(testResult.getCorrectCount()+1);
        }

        // 문제 2 채점
        Question question2 = new Question();
        question2.setNum(2);
        question2.setUserAnswer(String.valueOf(answer2));
        if(answer2==2) {
            question2.setIsCorrect(true);
            testResult.setCorrectCount(testResult.getCorrectCount()+1);
        }


        // 문제 3 채점
        Question question3 = new Question();
        question3.setNum(3);
        question3.setUserAnswer(answer3);

        List<LinkedHashMap<String, Object>> sqlResult = validateAndGetSqlResult(answer3, sqlData, bindingResult, question3); // 사용자의 답안을 검증하고 sql 결과를 가져온다.
        question3.setSqlResult(getSqlResultForShow(question3, sqlResult));

        List<Employee> employeeList = employeeRepository.findBySalaryGreaterThanEqual(5000);

        int correctCount=0;
        if(sqlResult!=null&&sqlResult.size()==2&&sqlResult.get(0).size()==8) {
            for(int i=0;i<2;i++) {
                if(employeeList.get(i).getId()==(int)sqlResult.get(i).get("ID"))correctCount++;
            }
        }
        if(correctCount==2) {
            question3.setIsCorrect(true);
            testResult.setCorrectCount(testResult.getCorrectCount()+1);
        }


        questionList.add(question1);
        questionList.add(question2);
        questionList.add(question3);
        testResult.setQuestionList(questionList);
        return testResult;
    }

    public TestResult gradeUnit10(Map<String, Object> userAnswer, SQLData sqlData, BindingResult bindingResult) {
        TestResult testResult = new TestResult();

        List<Question> questionList = new ArrayList<>();

        int answer1 = Integer.parseInt(userAnswer.get("question1").toString());
        int answer2 = Integer.parseInt(userAnswer.get("question2").toString());
        String answer3 = userAnswer.get("question3").toString();

        // 문제 1 채점
        Question question1 = new Question();
        question1.setNum(1);
        question1.setUserAnswer(String.valueOf(answer1));
        if(answer1==1) {
            question1.setIsCorrect(true);
            testResult.setCorrectCount(testResult.getCorrectCount()+1);
        }


        // 문제 2 채점
        Question question2 = new Question();
        question2.setNum(2);
        question2.setUserAnswer(String.valueOf(answer2));
        if(answer2==4) {
            question2.setIsCorrect(true);
            testResult.setCorrectCount(testResult.getCorrectCount()+1);
        }


        // 문제 3 채점
        Question question3 = new Question();
        question3.setNum(3);
        question3.setUserAnswer(answer3);

        List<LinkedHashMap<String, Object>> sqlResult = validateAndGetSqlResult(answer3, sqlData, bindingResult, question3); // 사용자의 답안을 검증하고 sql 결과를 가져온다.
        question3.setSqlResult(getSqlResultForShow(question3, sqlResult));

        List<EducationCost> educationCostList1 = educationCostRepository.findBytotalcostLessThan(3000);
        List<EducationCost> educationCostList2 = educationCostRepository.findByfirstgradeBetween(100, 500);

        List<EducationCost> educationCostList = new ArrayList<>();

        for(int i=0;i<educationCostList1.size();i++){
            if(educationCostList2.contains(educationCostList1.get(i)))
                educationCostList.add(educationCostList1.get(i));
        }

        int correctCount=0;
        if(sqlResult!=null&&sqlResult.size()==5&&sqlResult.get(0).size()==6) {
            for(int i=0;i<5;i++) {
                if(educationCostList.get(i).getId() == (int)sqlResult.get(i).get("ID"))correctCount++;
            }
        }
        if(correctCount==5) {
            question3.setIsCorrect(true);
            testResult.setCorrectCount(testResult.getCorrectCount()+1);
        }


        questionList.add(question1);
        questionList.add(question2);
        questionList.add(question3);
        testResult.setQuestionList(questionList);
        return testResult;
    }

    public TestResult gradeUnit11(Map<String, Object> userAnswer, SQLData sqlData, BindingResult bindingResult) {
        TestResult testResult = new TestResult();

        List<Question> questionList = new ArrayList<>();

        int answer1 = Integer.parseInt(userAnswer.get("question1").toString());
        int answer2 = Integer.parseInt(userAnswer.get("question2").toString());
        String answer3 = userAnswer.get("question3").toString();

        // 문제 1 채점
        Question question1 = new Question();
        question1.setNum(1);
        question1.setUserAnswer(String.valueOf(answer1));
        if(answer1==4) {
            question1.setIsCorrect(true);
            testResult.setCorrectCount(testResult.getCorrectCount()+1);
        }


        // 문제 2 채점
        Question question2 = new Question();
        question2.setNum(2);
        question2.setUserAnswer(String.valueOf(answer2));
        if(answer2==5) {
            question2.setIsCorrect(true);
            testResult.setCorrectCount(testResult.getCorrectCount()+1);
        }


        // 문제 3 채점
        Question question3 = new Question();
        question3.setNum(3);
        question3.setUserAnswer(answer3);

        List<LinkedHashMap<String, Object>> sqlResult = validateAndGetSqlResult(answer3, sqlData, bindingResult, question3); // 사용자의 답안을 검증하고 sql 결과를 가져온다.

        question3.setSqlResult(getSqlResultForShow(question3, sqlResult));

        List<CovidVaccinationCenter> covidVaccinationCenters = covidVaccinationCenterRepository.findAll();

        int correctCount = 0;
        int nullCount = 0;

        if(sqlResult!=null&&sqlResult.size()==100&&sqlResult.get(0).size()==1) {
            for(int i=0;i<100;i++) {
                if(!sqlResult.get(i).containsKey("IFNULL(PHONE, 'NO PHONE')"))
                    break;
                if(covidVaccinationCenters.get(i).getPhone() == null) {
                    nullCount++;
                    if(sqlResult.get(i).get("IFNULL(PHONE, 'NO PHONE')").equals("NO PHONE")) nullCount--;
                }
                else{
                    if(covidVaccinationCenters.get(i).getPhone().equals(sqlResult.get(i).get("IFNULL(PHONE, 'NO PHONE')").toString())) correctCount++;
                }
            }
        }

        if(correctCount == 99 && nullCount == 0) {
            question3.setIsCorrect(true);
            testResult.setCorrectCount(testResult.getCorrectCount()+1);
        }


        questionList.add(question1);
        questionList.add(question2);
        questionList.add(question3);
        testResult.setQuestionList(questionList);
        return testResult;
    }

    public TestResult gradeUnit12(Map<String, Object> userAnswer, SQLData sqlData, BindingResult bindingResult) {
        TestResult testResult = new TestResult();

        List<Question> questionList = new ArrayList<>();

        int answer1 = Integer.parseInt(userAnswer.get("question1").toString());
        int answer2 = Integer.parseInt(userAnswer.get("question2").toString());
        String answer3 = userAnswer.get("question3").toString();

        // 문제 1 채점
        Question question1 = new Question();
        question1.setNum(1);
        question1.setUserAnswer(String.valueOf(answer1));
        if(answer1==4) {
            question1.setIsCorrect(true);
            testResult.setCorrectCount(testResult.getCorrectCount()+1);
        }


        // 문제 2 채점
        Question question2 = new Question();
        question2.setNum(2);
        question2.setUserAnswer(String.valueOf(answer2));
        if(answer2==3) {
            question2.setIsCorrect(true);
            testResult.setCorrectCount(testResult.getCorrectCount()+1);
        }


        // 문제 3 채점
        Question question3 = new Question();
        question3.setNum(3);
        question3.setUserAnswer(answer3);

        List<LinkedHashMap<String, Object>> sqlResult = validateAndGetSqlResult(answer3, sqlData, bindingResult, question3); // 사용자의 답안을 검증하고 sql 결과를 가져온다.

        question3.setSqlResult(getSqlResultForShow(question3, sqlResult));

        List<RegionalRecoveryCostInfo> regionalRecoveryCostInfos = regionalRecoveryCostInfoRepository.findByRegionEndsWith("광역시");


        int correctCount=0;
        if(sqlResult!=null&&sqlResult.size()==6&&sqlResult.get(0).size()==7) {
            for(int i=0;i<6;i++) {
                if(regionalRecoveryCostInfos.get(i).getId() == (int)sqlResult.get(i).get("ID"))correctCount++;
            }
        }

        if(correctCount==6) {
            question3.setIsCorrect(true);
            testResult.setCorrectCount(testResult.getCorrectCount()+1);
        }


        questionList.add(question1);
        questionList.add(question2);
        questionList.add(question3);
        testResult.setQuestionList(questionList);
        return testResult;
    }

    public TestResult gradeUnit13(Map<String, Object> userAnswer, SQLData sqlData, BindingResult bindingResult) {
        TestResult testResult = new TestResult();

        int answer1 = Integer.parseInt(userAnswer.get("question1").toString());
        int answer2 = Integer.parseInt(userAnswer.get("question2").toString());
        String answer3 = userAnswer.get("question3").toString();

        List<Question> questionList = new ArrayList<>();

        // 문제 1 채점
        Question question1 = new Question();
        question1.setNum(1);
        question1.setUserAnswer(String.valueOf(answer1));
        if(answer1==4) {
            question1.setIsCorrect(true);
            testResult.setCorrectCount(testResult.getCorrectCount()+1);
        }


        // 문제 2 채점
        Question question2 = new Question();
        question2.setNum(2);
        question2.setUserAnswer(String.valueOf(answer2));
        if(answer2==3) {
            question2.setIsCorrect(true);
            testResult.setCorrectCount(testResult.getCorrectCount()+1);
        }


        // 문제 3 채점
        Question question3 = new Question();
        question3.setNum(3);
        question3.setUserAnswer(answer3);

        List<LinkedHashMap<String, Object>> sqlResult = validateAndGetSqlResult(answer3, sqlData, bindingResult, question3); // 사용자의 답안을 검증하고 sql 결과를 가져온다.
        question3.setSqlResult(getSqlResultForShow(question3, sqlResult)); // 사용자에게 보여줄 sql 결과를 List<List<String>> 타입으로 생성 후 저장
        showSqlResult(question3.getSqlResult()); // 유저가 생성한 sql 결과를 확인

        List<Employee> employeeList = employeeRepository.findAll(Sort.by(Sort.Direction.DESC, "salary")); // 답안

        int correctCount=0;
        if(sqlResult!=null&&sqlResult.size()==5&&sqlResult.get(0).size()==8) {
            for(int i=0;i<5;i++) {
                if(employeeList.get(i).getId() == (int)sqlResult.get(i).get("ID")) correctCount++;
            }
        }
        if(correctCount==5) {
            question3.setIsCorrect(true);
            testResult.setCorrectCount(testResult.getCorrectCount()+1);
        }


        questionList.add(question1);
        questionList.add(question2);
        questionList.add(question3);
        testResult.setQuestionList(questionList);
        return testResult;
    }

    public TestResult gradeUnit14(Map<String, Object> userAnswer, SQLData sqlData, BindingResult bindingResult) {
        TestResult testResult = new TestResult();

        int answer1 = Integer.parseInt(userAnswer.get("question1").toString());
        int answer2 = Integer.parseInt(userAnswer.get("question2").toString());
        int answer3 = Integer.parseInt(userAnswer.get("question3").toString());

        List<Question> questionList = new ArrayList<>();

        // 문제 1 채점
        Question question1 = new Question();
        question1.setNum(1);
        question1.setUserAnswer(String.valueOf(answer1));
        if(answer1==2) {
            question1.setIsCorrect(true);
            testResult.setCorrectCount(testResult.getCorrectCount()+1);
        }


        // 문제 2 채점
        Question question2 = new Question();
        question2.setNum(2);
        question2.setUserAnswer(String.valueOf(answer2));
        if(answer2==1) {
            question2.setIsCorrect(true);
            testResult.setCorrectCount(testResult.getCorrectCount()+1);
        }


        // 문제 3 채점
        Question question3 = new Question();
        question3.setNum(3);
        question3.setUserAnswer(String.valueOf(answer3));
        if(answer3==4) {
            question3.setIsCorrect(true);
            testResult.setCorrectCount(testResult.getCorrectCount()+1);
        }


        questionList.add(question1);
        questionList.add(question2);
        questionList.add(question3);
        testResult.setQuestionList(questionList);
        return testResult;
    }

    public TestResult gradeUnit15(Map<String, Object> userAnswer, SQLData sqlData, BindingResult bindingResult) {
        TestResult testResult = new TestResult();

        int answer1 = Integer.parseInt(userAnswer.get("question1").toString());
        int answer2 = Integer.parseInt(userAnswer.get("question2").toString());

        List<Question> questionList = new ArrayList<>();

        // 문제 1 채점
        Question question1 = new Question();
        question1.setNum(1);
        question1.setUserAnswer(String.valueOf(answer1));
        if(answer1==3) {
            question1.setIsCorrect(true);
            testResult.setCorrectCount(testResult.getCorrectCount()+1);
        }


        // 문제 2 채점
        Question question2 = new Question();
        question2.setNum(2);
        question2.setUserAnswer(String.valueOf(answer2));
        if(answer2==5) {
            question2.setIsCorrect(true);
            testResult.setCorrectCount(testResult.getCorrectCount()+1);
        }

        questionList.add(question1);
        questionList.add(question2);
        testResult.setQuestionList(questionList);
        return testResult;
    }

    public TestResult gradeUnit16(Map<String, Object> userAnswer, SQLData sqlData, BindingResult bindingResult) {
        TestResult testResult = new TestResult();

        int answer1 = Integer.parseInt(userAnswer.get("question1").toString());
        int answer2 = Integer.parseInt(userAnswer.get("question2").toString());
        String answer3 = userAnswer.get("question3").toString();

        List<Question> questionList = new ArrayList<>();

        // 문제 1 채점
        Question question1 = new Question();
        question1.setNum(1);
        question1.setUserAnswer(String.valueOf(answer1));
        if(answer1==3) {
            question1.setIsCorrect(true);
            testResult.setCorrectCount(testResult.getCorrectCount()+1);
        }


        // 문제 2 채점
        Question question2 = new Question();
        question2.setNum(2);
        question2.setUserAnswer(String.valueOf(answer2));
        if(answer2==2) {
            question2.setIsCorrect(true);
            testResult.setCorrectCount(testResult.getCorrectCount()+1);
        }


        // 문제 3 채점
        Question question3 = new Question();
        question3.setNum(3);
        question3.setUserAnswer(answer3);

        List<LinkedHashMap<String, Object>> sqlResult = validateAndGetSqlResult(answer3, sqlData, bindingResult, question3); // 사용자의 답안을 검증하고 sql 결과를 가져온다.
        question3.setSqlResult(getSqlResultForShow(question3, sqlResult)); // 사용자에게 보여줄 sql 결과를 List<List<String>> 타입으로 생성 후 저장

        // 답 : SELECT A.WORK FROM ASSIGNMENT AS A LEFT JOIN LEISURE AS L ON A.EMPLOYEE_ID=L.EMPLOYEE_ID WHERE L.EMPLOYEE_ID IS NULL;
        JPAQueryFactory query = new JPAQueryFactory(em);
        QAssignment qa = QAssignment.assignment;
        QLeisure ql = QLeisure.leisure;
        List<String> resultList = query.select(qa.work)
                .from(qa).leftJoin(ql)
                .on(qa.employeeId.eq(ql.employeeId))
                .where(ql.hobby.isNull())
                .fetch();

        int correctCount=0;
        if(sqlResult!=null&&sqlResult.size()==3&&sqlResult.get(0).size()==1) {
            for(int i=0;i<3;i++) {
                if(sqlResult.get(i).containsKey("WORK")&&sqlResult.get(i).get("WORK").toString().equals(resultList.get(i))) correctCount++;
            }
        }
        if(correctCount==3) {
            question3.setIsCorrect(true);
            testResult.setCorrectCount(testResult.getCorrectCount()+1);
        }

        questionList.add(question1);
        questionList.add(question2);
        questionList.add(question3);
        testResult.setQuestionList(questionList);
        return testResult;
    }

    public TestResult gradeUnit17(Map<String, Object> userAnswer, SQLData sqlData, BindingResult bindingResult) {
        TestResult testResult = new TestResult();

        int answer1 = Integer.parseInt(userAnswer.get("question1").toString());
        int answer2 = Integer.parseInt(userAnswer.get("question2").toString());
        String answer3 = userAnswer.get("question3").toString();

        List<Question> questionList = new ArrayList<>();

        // 문제 1 채점
        Question question1 = new Question();
        question1.setNum(1);
        question1.setUserAnswer(String.valueOf(answer1));
        if(answer1==3) {
            question1.setIsCorrect(true);
            testResult.setCorrectCount(testResult.getCorrectCount()+1);
        }


        // 문제 2 채점
        Question question2 = new Question();
        question2.setNum(2);
        question2.setUserAnswer(String.valueOf(answer2));
        if(answer2==5) {
            question2.setIsCorrect(true);
            testResult.setCorrectCount(testResult.getCorrectCount()+1);
        }


        // 문제 3 채점
        Question question3 = new Question();
        question3.setNum(3);
        question3.setUserAnswer(answer3);

        List<LinkedHashMap<String, Object>> sqlResult = validateAndGetSqlResult(answer3, sqlData, bindingResult, question3); // 사용자의 답안을 검증하고 sql 결과를 가져온다.
        question3.setSqlResult(getSqlResultForShow(question3, sqlResult)); // 사용자에게 보여줄 sql 결과를 List<List<String>> 타입으로 생성 후 저장

        // SELECT STUDENT_ID, STUDENT_NAME FROM MENTEE_MENTO WHERE MENTO_ID IN(SELECT MENTO_ID FROM MENTEE_MENTO GROUP BY MENTO_ID HAVING COUNT(MENTO_ID)>=3);
        JPAQueryFactory query = new JPAQueryFactory(em);
        QMenteeMento qe = QMenteeMento.menteeMento;
        List<MenteeMento> resultList = query.selectFrom(qe)
                .where(qe.mentoId.in(
                        JPAExpressions.select(qe.mentoId)
                                .from(qe)
                                .groupBy(qe.mentoId)
                                .having(qe.mentoId.count().goe(3))
                ))
                .fetch();

        int correctCount=0;
        if(sqlResult!=null&&sqlResult.size()==9&&sqlResult.get(0).size()==2) {
            for(int i=0;i<9;i++) {
                if(sqlResult.get(i).containsKey("STUDENT_ID")&&sqlResult.get(i).get("STUDENT_ID").toString().equals(String.valueOf(resultList.get(i).getStudentId()))) correctCount++;
                if(sqlResult.get(i).containsKey("STUDENT_NAME")&&sqlResult.get(i).get("STUDENT_NAME").toString().equals(resultList.get(i).getStudentName())) correctCount++;
            }
        }
        if(correctCount==18) {
            question3.setIsCorrect(true);
            testResult.setCorrectCount(testResult.getCorrectCount()+1);
        }

        questionList.add(question1);
        questionList.add(question2);
        questionList.add(question3);
        testResult.setQuestionList(questionList);
        return testResult;
    }
}