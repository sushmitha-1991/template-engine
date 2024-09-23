package repository;

import model.GeneratedPdfModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface GeneratedPdfModelRepository extends JpaRepository<GeneratedPdfModel,Long> {

    @Query("select pdf from GeneratedPdfModel pdf where refId=:refId and refType=:refType order by id desc")
    Optional<GeneratedPdfModel> findTopByRefIdAndRefType(Long refId, String refType);
}
