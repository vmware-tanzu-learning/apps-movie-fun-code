package org.superbiz.moviefun;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
public class Application {

    public static void main(String... args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public ServletRegistrationBean actionServletRegistration(ActionServlet actionServlet) {
        return new ServletRegistrationBean(actionServlet, "/moviefun/*");
    }

    @Bean
    public DatabaseServiceCredentials getVcaps( @Value("${VCAP_SERVICES}") String vcapsServiceInfo) {
        return new DatabaseServiceCredentials(vcapsServiceInfo);
    }

    @Bean
    public DataSource albumsDataSource(DatabaseServiceCredentials serviceCredentials) {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(serviceCredentials.jdbcUrl("albums-mysql", "p-mysql"));
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDataSource(dataSource);
        return new HikariDataSource(hikariConfig);
    }

    @Bean
    public DataSource moviesDataSource(DatabaseServiceCredentials serviceCredentials) {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(serviceCredentials.jdbcUrl("movies-mysql", "p-mysql"));
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDataSource(dataSource);
        return new HikariDataSource(hikariConfig);
    }

    @Bean
    public HibernateJpaVendorAdapter jpaVendorAdaptor() {

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setDatabase(Database.MYSQL);
        vendorAdapter.setDatabasePlatform("org.hibernate.dialect.MySQL5Dialect");
        vendorAdapter.setGenerateDdl(true);
        return vendorAdapter;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean movieEntityManagerFactory(DataSource moviesDataSource) {

        LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactory.setDataSource(moviesDataSource);
        entityManagerFactory.setJpaVendorAdapter(jpaVendorAdaptor());
        entityManagerFactory.setPackagesToScan("org.superbiz.moviefun.movies");
        entityManagerFactory.setPersistenceUnitName("movies");


        return entityManagerFactory;
    }
    @Bean
    public LocalContainerEntityManagerFactoryBean albumEntityManagerFactory(DataSource albumsDataSource) {

        LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactory.setDataSource(albumsDataSource);
        entityManagerFactory.setJpaVendorAdapter(jpaVendorAdaptor());
        entityManagerFactory.setPackagesToScan("org.superbiz.moviefun.albums");
        entityManagerFactory.setPersistenceUnitName("albums");

        return entityManagerFactory;
    }

    @Bean
    public PlatformTransactionManager moviesTransactionManager(EntityManagerFactory movieEntityManagerFactory){

        return new JpaTransactionManager(movieEntityManagerFactory);
    }

    @Bean
    public PlatformTransactionManager albumsTransactionManager(EntityManagerFactory albumEntityManagerFactory){

        return new JpaTransactionManager(albumEntityManagerFactory);
    }

}
