package org.superbiz.moviefun;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.inject.Named;
import javax.sql.DataSource;

@Configuration
public class DbConfig {

    @Bean
    public DataSource moviesDataSource(
            @Value("${moviefun.datasources.movies.url}") String url,
            @Value("${moviefun.datasources.movies.username}") String userName,
            @Value("${moviefun.datasources.movies.password}") String password){
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setJdbcUrl(url);
        hikariDataSource.setUsername(userName);
        hikariDataSource.setPassword(password);

        return hikariDataSource;
    }

    @Bean
    public DataSource albumsDataSource(
           @Value("${moviefun.datasources.albums.url}") String url,
           @Value("${moviefun.datasources.albums.username}") String userName,
           @Value("${moviefun.datasources.albums.password}") String password){

        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setJdbcUrl(url);
        hikariDataSource.setUsername(userName);
        hikariDataSource.setPassword(password);

        return hikariDataSource;
    }

    @Bean
    public HibernateJpaVendorAdapter hibernateAdapter(){
        HibernateJpaVendorAdapter hibernateAdapter = new HibernateJpaVendorAdapter();
        hibernateAdapter.setGenerateDdl(true);
        hibernateAdapter.setDatabasePlatform("org.hibernate.dialect.MySQL5Dialect");
        hibernateAdapter.setDatabase(Database.MYSQL);

        return hibernateAdapter;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean movieEntityManagerFactory(
            @Qualifier("moviesDataSource") DataSource movieDataSource,
            HibernateJpaVendorAdapter hibernateAdapter){
        LocalContainerEntityManagerFactoryBean factoryBean =
                new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(movieDataSource);
        factoryBean.setJpaVendorAdapter(hibernateAdapter);
        factoryBean.setPackagesToScan("org.superbiz.moviefun.movies");
        factoryBean.setPersistenceUnitName("moviesPU");

        return factoryBean;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean albumsEntityManagerFactory(
            @Qualifier("albumsDataSource") DataSource albumsDataSource,
            HibernateJpaVendorAdapter hibernateAdapter){
        LocalContainerEntityManagerFactoryBean factoryBean =
                new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(albumsDataSource);
        factoryBean.setJpaVendorAdapter(hibernateAdapter);
        factoryBean.setPackagesToScan("org.superbiz.moviefun.albums");
        factoryBean.setPersistenceUnitName("albumsPU");

        return factoryBean;
    }

    @Bean
    public PlatformTransactionManager moviesTransactionManager (
            @Qualifier ("movieEntityManagerFactory") LocalContainerEntityManagerFactoryBean entityManagerFactoryBean
    ){
        JpaTransactionManager moviesTransMgr =
                new JpaTransactionManager();
        moviesTransMgr.setEntityManagerFactory(entityManagerFactoryBean.getObject());
        return moviesTransMgr;
    }

    @Bean
    public PlatformTransactionManager albumsTransactionManager (
            @Qualifier ("albumsEntityManagerFactory") LocalContainerEntityManagerFactoryBean entityManagerFactoryBean
    ){
        JpaTransactionManager albumsTransMgr =
                new JpaTransactionManager();
        albumsTransMgr.setEntityManagerFactory(entityManagerFactoryBean.getObject());
        return albumsTransMgr;
    }
}
