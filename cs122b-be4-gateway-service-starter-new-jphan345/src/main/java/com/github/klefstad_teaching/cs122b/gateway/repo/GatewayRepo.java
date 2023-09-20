package com.github.klefstad_teaching.cs122b.gateway.repo;

import com.github.klefstad_teaching.cs122b.gateway.model.data.GatewayRequestObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.sql.Types;
import java.util.List;

@Component
public class GatewayRepo
{
    private final NamedParameterJdbcTemplate template;
    @Autowired
    public GatewayRepo(NamedParameterJdbcTemplate template)
    {
        this.template = template;
    }

    /**
     *  Wraps the insertRequests function into a Mono.fromCallable()
     *  to prevent blocking the gateway
     **/
    public Mono<int[]> createInsertMono(List<GatewayRequestObject> requests)
    {
        return Mono.fromCallable(() -> insertRequests(requests));
    }
    public int[] insertRequests(List<GatewayRequestObject> requests)
    {
        // language=sql
        String sql = "INSERT INTO gateway.request (ip_address, call_time, path) " +
                     "VALUES (:ipAddress, :callTime, :path) ";

        MapSqlParameterSource[] arrayOfSources = requests.stream().map(
                request -> new MapSqlParameterSource()
                        .addValue("ipAddress", request.getIpAddress(), Types.VARCHAR)
                        .addValue("callTime", request.getCallTime(), Types.TIMESTAMP)
                        .addValue("path", request.getPath(), Types.VARCHAR)
        ).toArray(MapSqlParameterSource[]::new);

        return this.template.batchUpdate(sql, arrayOfSources);
    }
}
