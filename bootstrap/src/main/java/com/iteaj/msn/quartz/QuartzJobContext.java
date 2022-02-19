package com.iteaj.msn.quartz;

import lombok.Data;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;

@Data
public class QuartzJobContext {

    private Object bean;
    private Method method;
    private static ExpressionParser parser=new SpelExpressionParser();

    public QuartzJobContext(Object bean, Method method) {
        this.bean = bean;
        this.method = method;
    }

    public Object invoke(String expression, JobExecutionContext jobExecutionContext) {
        final JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();

        final StandardEvaluationContext context = new StandardEvaluationContext();

        context.setVariable("bean", bean);
        context.setVariable("data", jobDataMap);
        context.setVariable("context", jobExecutionContext);
        return parser.parseExpression("#bean."+expression).getValue(context);
    }

}
