package org.superbiz.moviefun;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.superbiz.moviefun.albums.Album;
import org.superbiz.moviefun.albums.AlbumFixtures;
import org.superbiz.moviefun.albums.AlbumsBean;
import org.superbiz.moviefun.movies.Movie;
import org.superbiz.moviefun.movies.MovieFixtures;
import org.superbiz.moviefun.movies.MoviesBean;

import java.util.Map;

@Controller
public class HomeController {

    private final MoviesBean moviesBean;
    private final AlbumsBean albumsBean;
    private final MovieFixtures movieFixtures;
    private final AlbumFixtures albumFixtures;
    private final PlatformTransactionManager moviesTransMgr;
    private final PlatformTransactionManager albumsTransMgr;

    public HomeController(MoviesBean moviesBean, AlbumsBean albumsBean,
                          MovieFixtures movieFixtures, AlbumFixtures albumFixtures,
                          @Qualifier ("moviesTransactionManager") PlatformTransactionManager moviesTransMgr,
                          @Qualifier ("albumsTransactionManager") PlatformTransactionManager albumsTransMgr) {
        this.moviesBean = moviesBean;
        this.albumsBean = albumsBean;
        this.movieFixtures = movieFixtures;
        this.albumFixtures = albumFixtures;
        this.moviesTransMgr = moviesTransMgr;
        this.albumsTransMgr = albumsTransMgr;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/setup")
    public String setup(Map<String, Object> model) {
        TransactionStatus moviesTransStatus = moviesTransMgr.getTransaction(null);
        for (Movie movie : movieFixtures.load()) {
            moviesBean.addMovie(movie);
        }
        moviesTransMgr.commit(moviesTransStatus);

        TransactionStatus albumsTransStatus = albumsTransMgr.getTransaction(null);
        for (Album album : albumFixtures.load()) {
            albumsBean.addAlbum(album);
        }
        albumsTransMgr.commit(albumsTransStatus);

        model.put("movies", moviesBean.getMovies());
        model.put("albums", albumsBean.getAlbums());

        return "setup";
    }
}
