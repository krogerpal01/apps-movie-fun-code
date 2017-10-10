package org.superbiz.moviefun;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
public class DbConfig {
    @Bean("albumsDB")
    public DataSource albumsDataSource(
            @Value("${moviefun.datasources.albums.url}") String url,
            @Value("${moviefun.datasources.albums.username}") String username,
            @Value("${moviefun.datasources.albums.password}") String password
    ) {
        return createDataSource(url, username, password);
    }

    @Bean("moviesDB")
    public DataSource moviesDataSource(
            @Value("${moviefun.datasources.movies.url}") String url,
            @Value("${moviefun.datasources.movies.username}") String username,
            @Value("${moviefun.datasources.movies.password}") String password
    ) {
        return createDataSource(url, username, password);
    }

    @Bean
    public HibernateJpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setDatabasePlatform("org.hibernate.dialect.MySQL5Dialect");
        vendorAdapter.setGenerateDdl(true);

        return vendorAdapter;
    }

    @Bean("albums")
    public LocalContainerEntityManagerFactoryBean albumsEmFactoryBean(@Qualifier("albumsDB") DataSource dataSource, HibernateJpaVendorAdapter adapter) {
        return factoryBean(dataSource, "albums", adapter);
    }

    @Bean("movies")
    public LocalContainerEntityManagerFactoryBean moviesEmFactoryBean(@Qualifier("moviesDB") DataSource dataSource, HibernateJpaVendorAdapter adapter) {
        return factoryBean(dataSource, "movies", adapter);
    }

    private static LocalContainerEntityManagerFactoryBean factoryBean(DataSource ds, String name, HibernateJpaVendorAdapter adapter) {
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(ds);
        factoryBean.setJpaVendorAdapter(adapter);
        factoryBean.setPackagesToScan("org.superbiz.moviefun");
        factoryBean.setPersistenceUnitName(name);

        return factoryBean;
    }

    @Bean("albumsTM")
    public PlatformTransactionManager albumsTransactionManager(@Qualifier("albums") LocalContainerEntityManagerFactoryBean factoryBean) {
        EntityManagerFactory emf = factoryBean.getObject();
        return new JpaTransactionManager(emf);
    }

    @Bean("moviesTM")
    public PlatformTransactionManager moviesTransactionManager(@Qualifier("movies") LocalContainerEntityManagerFactoryBean factoryBean) {
        EntityManagerFactory emf = factoryBean.getObject();
        return new JpaTransactionManager(emf);
    }

    private static DataSource createDataSource(String url, String username, String password) {
        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl(url);
        cfg.setUsername(username);
        cfg.setPassword(password);
        return new HikariDataSource(cfg);
//        MysqlDataSource dataSource = new MysqlDataSource();
//        dataSource.setURL(url);
//        dataSource.setUser(username);
//        dataSource.setPassword(password);
//        return dataSource;
    }
}
