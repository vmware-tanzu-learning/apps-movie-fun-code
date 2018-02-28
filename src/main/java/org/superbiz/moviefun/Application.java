package org.superbiz.moviefun;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;

import javax.sql.DataSource;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class Application {

    public static void main(String... args) {
        SpringApplication.run(Application.class, args);
    }


    @Bean
    public ServletRegistrationBean actionServletRegistration(ActionServlet actionServlet) {
        return new ServletRegistrationBean(actionServlet, "/moviefun/*");
    }

    @Bean(name="albumsDatasource")
    public DataSource albumsDatasource(DatabaseServiceCredentials serviceCredentials) {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(serviceCredentials.jdbcUrl("albums-mysql", "p-mysql"));
        HikariDataSource hkDataSource = new HikariDataSource();
        hkDataSource.setDataSource(dataSource);
        return hkDataSource;
    }

    @Bean(name="moviesDatasource")
    public DataSource moviesDatasource(DatabaseServiceCredentials serviceCredentials) {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(serviceCredentials.jdbcUrl("movies-mysql", "p-mysql"));
        HikariDataSource hkDataSource = new HikariDataSource();
        hkDataSource.setDataSource(dataSource);
        return hkDataSource;
    }

    @Bean
    public HibernateJpaVendorAdapter hibernateJpaVendorAdapter() {
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setGenerateDdl(true);
        adapter.setDatabasePlatform("org.hibernate.dialect.MySQL5Dialect");
        adapter.setDatabase(Database.MYSQL);
        return adapter;
    }

    @Bean(name="albumsEntityManager")
    public LocalContainerEntityManagerFactoryBean albumsEntityManagerFactoryBean(@Qualifier("albumsDatasource") DataSource albumsDataSource) {
        LocalContainerEntityManagerFactoryBean albumsEntityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        albumsEntityManagerFactoryBean.setDataSource(albumsDataSource);
        albumsEntityManagerFactoryBean.setJpaVendorAdapter(hibernateJpaVendorAdapter());
        albumsEntityManagerFactoryBean.setPackagesToScan("org.superbiz.moviefun");
        albumsEntityManagerFactoryBean.setPersistenceUnitName("albumsPU");

        return albumsEntityManagerFactoryBean;
    }

    @Bean(name="moviesEntityManager")
    public LocalContainerEntityManagerFactoryBean moviesEntityManagerFactoryBean(@Qualifier("moviesDatasource") DataSource moviesDataSource) {
        LocalContainerEntityManagerFactoryBean moviesEntityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        moviesEntityManagerFactoryBean.setDataSource(moviesDataSource);
        moviesEntityManagerFactoryBean.setJpaVendorAdapter(hibernateJpaVendorAdapter());
        moviesEntityManagerFactoryBean.setPackagesToScan("org.superbiz.moviefun");
        moviesEntityManagerFactoryBean.setPersistenceUnitName("moviesPU");

        return moviesEntityManagerFactoryBean;
    }

    @Bean(name="albumsTransactionManager")
    public PlatformTransactionManager albumsTransactionManager(@Qualifier("albumsEntityManager") LocalContainerEntityManagerFactoryBean  albumsEntityManager) {
        return new JpaTransactionManager(albumsEntityManager.getObject());
    }

    @Bean(name="moviesTransactionManager")
    public PlatformTransactionManager moviesTransactionManager(@Qualifier("moviesEntityManager") LocalContainerEntityManagerFactoryBean  moviesEntityManager) {
        return new JpaTransactionManager(moviesEntityManager.getObject());
    }

}
