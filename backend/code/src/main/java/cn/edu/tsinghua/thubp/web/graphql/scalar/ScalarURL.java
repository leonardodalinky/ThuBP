package cn.edu.tsinghua.thubp.web.graphql.scalar;

import graphql.language.StringValue;
import graphql.schema.*;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.Instant;

@Component
public class ScalarURL extends GraphQLScalarType {
    public ScalarURL() {
        //noinspection rawtypes
        super("URL", "Scalar URL", new Coercing() {
            @Override
            public Object serialize(Object o) throws CoercingSerializeException {
                return ((URL) o).toString();
            }

            @Override
            public Object parseValue(Object o) throws CoercingParseValueException {
                return serialize(o);
            }

            @SneakyThrows
            @Override
            public Object parseLiteral(Object o) throws CoercingParseLiteralException {
                return new URL(((StringValue) o).getValue());
            }
        });
    }
}