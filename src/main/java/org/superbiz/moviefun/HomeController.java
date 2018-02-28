package org.superbiz.moviefun;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.bind.annotation.GetMapping;
import org.superbiz.moviefun.albums.Album;
import org.superbiz.moviefun.albums.AlbumFixtures;
import org.superbiz.moviefun.albums.AlbumsBean;
import org.superbiz.moviefun.movies.Movie;
import org.superbiz.moviefun.movies.MovieFixtures;
import org.superbiz.moviefun.movies.MoviesBean;

import javax.transaction.*;
import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    @Qualifier("moviesTransactionManager")
    private PlatformTransactionManager moviesTransactionManager;

    @Autowired
    @Qualifier("albumsTransactionManager")
    private PlatformTransactionManager albumsTransactionManager;

    private final MoviesBean moviesBean;
    private final AlbumsBean albumsBean;
    private final MovieFixtures movieFixtures;
    private final AlbumFixtures albumFixtures;

    public HomeController(MoviesBean moviesBean, AlbumsBean albumsBean, MovieFixtures movieFixtures, AlbumFixtures albumFixtures) {
        this.moviesBean = moviesBean;
        this.albumsBean = albumsBean;
        this.movieFixtures = movieFixtures;
        this.albumFixtures = albumFixtures;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/setup")
    public String setup(Map<String, Object> model) throws Exception {
        for (Movie movie : movieFixtures.load()) {
            TransactionStatus transactionStatus = moviesTransactionManager.getTransaction(new DefaultTransactionDefinition());
            moviesBean.addMovie(movie);
            moviesTransactionManager.commit(transactionStatus);


        }

        for (Album album : albumFixtures.load()) {
            TransactionStatus transactionStatus = albumsTransactionManager.getTransaction(new DefaultTransactionDefinition());
            albumsBean.addAlbum(album);
            albumsTransactionManager.commit(transactionStatus);
        }

        model.put("movies", moviesBean.getMovies());
        model.put("albums", albumsBean.getAlbums());

        return "setup";
    }
}
