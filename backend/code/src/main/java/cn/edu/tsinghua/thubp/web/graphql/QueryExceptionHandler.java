package cn.edu.tsinghua.thubp.web.graphql;


import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.language.SourceLocation;
import graphql.servlet.GraphQLErrorHandler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class QueryExceptionHandler implements GraphQLErrorHandler {
    @Override
    public List<GraphQLError> processErrors(List<GraphQLError> list) {
        List<GraphQLError> errorList = new ArrayList<>();
        // wrap 一下，防止 stack trace 被返回出去
        for (GraphQLError error : list){
            errorList.add(new GraphQLError() {
                @Override
                public String getMessage() {
                    return error.getMessage();
                }

                @Override
                public List<SourceLocation> getLocations() {
                    return null;
                }

                @Override
                public ErrorType getErrorType() {
                    return error.getErrorType();
                }

                @Override
                public Map<String, Object> getExtensions() {
                    return error.getExtensions();
                }
            });
        }
        return errorList;
    }
}
