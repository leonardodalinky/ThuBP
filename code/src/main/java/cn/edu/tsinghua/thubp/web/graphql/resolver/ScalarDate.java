package cn.edu.tsinghua.thubp.web.graphql.resolver;

import graphql.language.StringValue;
import graphql.schema.*;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class ScalarDate extends GraphQLScalarType {
    public ScalarDate() {
        //noinspection rawtypes
        super("Date", "Scalar Date", new Coercing() {
            @Override
            public Object serialize(Object o) throws CoercingSerializeException {
                return ((Instant) o).toString();
            }

            @Override
            public Object parseValue(Object o) throws CoercingParseValueException {
                return serialize(o);
            }

            @Override
            public Object parseLiteral(Object o) throws CoercingParseLiteralException {
                return Instant.parse(((StringValue) o).getValue());
            }
        });
    }
}