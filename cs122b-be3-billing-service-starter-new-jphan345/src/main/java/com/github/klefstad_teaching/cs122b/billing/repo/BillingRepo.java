package com.github.klefstad_teaching.cs122b.billing.repo;

import com.github.klefstad_teaching.cs122b.billing.model.data.Discount;
import com.github.klefstad_teaching.cs122b.billing.model.data.Movie;
import com.github.klefstad_teaching.cs122b.billing.model.data.Sale;
import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.BillingResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Component
public class BillingRepo
{
    private final NamedParameterJdbcTemplate template;

    @Autowired
    public BillingRepo(NamedParameterJdbcTemplate template)
    {
        this.template = template;
    }

    public Discount getDiscount(long movieId) {
        String sql =
                "SELECT premium_discount " +
                "    FROM billing.movie_price mp " +
                "    WHERE mp.movie_id = :movieId";

        MapSqlParameterSource source = new MapSqlParameterSource()
                .addValue("movieId", movieId, Types.INTEGER);

        return this.template.queryForObject(
                sql,
                source,
                (ResultSet rs, int rowNum) ->
                        new Discount()
                                .setDiscount(rs.getInt("premium_discount"))
        );
    }

    public void cartItemExists(long userId, long movieId) {
        //language=sql
        String sql =
                "SELECT * " +
                "FROM billing.cart c " +
                "WHERE user_id = :userId AND movie_id = :movieId";

        MapSqlParameterSource source = new MapSqlParameterSource()
                .addValue("userId", userId, Types.INTEGER)
                .addValue("movieId", movieId, Types.INTEGER);

        try {
            this.template.queryForObject(
                    sql,
                    source,
                    (rs, rowNum) ->
                            new Movie()
            );
        } catch (DataAccessException e) {
            throw new ResultError(BillingResults.CART_ITEM_DOES_NOT_EXIST);
        }
    }


    public void cartInsert(long userId, long movieId, int quantity) {
        //language=sql
        String sql =
                "INSERT INTO billing.cart (user_id, movie_id, quantity) " +
                "VALUES  (:userId, :movieId, :quantity)";

        MapSqlParameterSource source = new MapSqlParameterSource()
                .addValue("userId", userId, Types.INTEGER)
                .addValue("movieId", movieId, Types.INTEGER)
                .addValue("quantity", quantity, Types.INTEGER);

        try {
            this.template.update(sql, source);
        } catch (DataAccessException e) {
            throw new ResultError(BillingResults.CART_ITEM_EXISTS);
        }
    }

    public void cartUpdate(long userId, long movieId, int quantity) {
        //language=sql
        String sql =
                "UPDATE billing.cart " +
                "SET quantity = :quantity " +
                "WHERE user_id = :userId AND movie_id = :movieId";

        MapSqlParameterSource source = new MapSqlParameterSource()
                .addValue("userId", userId, Types.INTEGER)
                .addValue("movieId", movieId, Types.INTEGER)
                .addValue("quantity", quantity, Types.INTEGER);

        try {
            this.template.update(sql, source);
        } catch (DataAccessException e) {
            throw new ResultError(BillingResults.CART_ITEM_DOES_NOT_EXIST);
        }
    }

    public void cartDelete(long userId, long movieId) {
        //language=sql
        String sql =
                "DELETE FROM billing.cart " +
                "WHERE user_id = :userId AND movie_id = :movieId";

        MapSqlParameterSource source = new MapSqlParameterSource()
                .addValue("userId", userId, Types.INTEGER)
                .addValue("movieId", movieId, Types.INTEGER);

        try {
            this.template.update(sql, source);
        } catch (DataAccessException e) {
            throw new ResultError(BillingResults.CART_ITEM_DOES_NOT_EXIST);
        }
    }

    public List<Movie> cartRetrieve(long userId) {
        String sql =
                "SELECT mp.unit_price, c.quantity, c.movie_id, m.title, m.backdrop_path, m.poster_path " +
                "    FROM billing.cart AS c " +
                "        INNER JOIN idm.user u ON u.id = c.user_id " +
                "        INNER JOIN movies.movie m ON m.id = c.movie_id " +
                "        INNER JOIN billing.movie_price mp ON mp.movie_id = c.movie_id " +
                "    WHERE c.user_id = :userId";

        MapSqlParameterSource source = new MapSqlParameterSource()
                .addValue("userId", userId, Types.INTEGER);

        return this.template.query(
                sql,
                source,
                (rs, rowNum) ->
                        new Movie()
                                .setUnitPrice(rs.getBigDecimal("unit_price"))
                                .setQuantity(rs.getInt("quantity"))
                                .setMovieId(rs.getLong("movie_id"))
                                .setMovieTitle(rs.getString("title"))
                                .setBackdropPath(rs.getString("backdrop_path"))
                                .setPosterPath(rs.getString("poster_path"))
        );
    }

    public int cartClear(long userId) {
        String sql = "DELETE FROM billing.cart c WHERE c.user_id = :userId";

        MapSqlParameterSource source = new MapSqlParameterSource()
                .addValue("userId", userId, Types.INTEGER);

        return this.template.update(
                sql,
                source
        );
    }

    public Sale saleInsert(long userId, Sale sale) {
        BigDecimal total = sale.getTotal();
        Instant time = sale.getOrderDate();
        //language=sql
        String sql =
                "INSERT INTO billing.sale (user_id, total, order_date) " +
                "VALUES  (:userId, :total, :time)";

        MapSqlParameterSource source = new MapSqlParameterSource()
                .addValue("userId", userId, Types.INTEGER)
                .addValue("total", total, Types.DECIMAL)
                .addValue("time", Timestamp.from(time), Types.TIMESTAMP);

        try {
            this.template.update(sql, source);

        } catch (DataAccessException e) {
            throw new ResultError(BillingResults.ORDER_CANNOT_COMPLETE_NOT_SUCCEEDED);
        }

        // return the Sale object with the sale id
        List<Sale> sales = this.template.query(
                "SELECT s.id, s.order_date " +
                        "   FROM billing.sale s " +
                        "   WHERE user_id = :userId " +
                        "   ORDER BY order_date DESC",
                new MapSqlParameterSource()
                        .addValue("userId", userId, Types.INTEGER),
                (rs, rowNum) ->
                        new Sale()
                                .setSaleId(rs.getInt("id"))
                                .setOrderDate(rs.getTimestamp("order_date").toInstant())
        );

        sale.setSaleId(sales.get(0).getSaleId());
        return sale;
    }

    public void saleItemInsert(long saleId, long movieId, int quantity) {
        //language=sql
        String sql =
                "INSERT INTO billing.sale_item (sale_id, movie_id, quantity) " +
                "   VALUES  (:saleId, :movieId, :quantity)";

        MapSqlParameterSource source = new MapSqlParameterSource()
                .addValue("saleId", saleId, Types.INTEGER)
                .addValue("movieId", movieId, Types.INTEGER)
                .addValue("quantity", quantity, Types.INTEGER);

        try {
            this.template.update(sql, source);

        } catch (DataAccessException e) {
            throw new ResultError(BillingResults.ORDER_CANNOT_COMPLETE_NOT_SUCCEEDED);
        }
    }

    public List<Sale> saleList(long userId) {
        // return the Sale object with the sale id
        return this.template.query(
                "SELECT s.id, s.total, s.order_date " +
                        "   FROM billing.sale s " +
                        "   WHERE user_id = :userId " +
                        "   ORDER BY order_date DESC" +
                        "   LIMIT 5",
                new MapSqlParameterSource()
                        .addValue("userId", userId, Types.INTEGER),
                (rs, rowNum) ->
                        new Sale()
                                .setSaleId(rs.getInt("id"))
                                .setTotal(rs.getBigDecimal("total"))
                                .setOrderDate(rs.getTimestamp("order_date").toInstant())
        );
    }

    public List<Movie> saleGet(long userId, long saleId) {
        String sql =
                "SELECT mp.unit_price, si.quantity, si.movie_id, m.title, m.backdrop_path, m.poster_path " +
                        "    FROM billing.sale AS s " +
                        "        INNER JOIN billing.sale_item si ON s.id = si.sale_id " +
                        "        INNER JOIN movies.movie m ON m.id = si.movie_id " +
                        "        INNER JOIN billing.movie_price mp ON mp.movie_id = si.movie_id " +
                        "    WHERE si.sale_id = :saleId AND s.user_id = :userId";

        MapSqlParameterSource source = new MapSqlParameterSource()
                .addValue("saleId", saleId, Types.INTEGER)
                .addValue("userId", userId, Types.INTEGER);

        return this.template.query(
                sql,
                source,
                (rs, rowNum) ->
                        new Movie()
                                .setUnitPrice(rs.getBigDecimal("unit_price"))
                                .setQuantity(rs.getInt("quantity"))
                                .setMovieId(rs.getLong("movie_id"))
                                .setMovieTitle(rs.getString("title"))
                                .setBackdropPath(rs.getString("backdrop_path"))
                                .setPosterPath(rs.getString("poster_path"))
        );
    }
}
