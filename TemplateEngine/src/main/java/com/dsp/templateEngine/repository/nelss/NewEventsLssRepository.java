package com.dsp.templateEngine.repository.nelss;

import com.dsp.templateEngine.entity.nelss.NewEventsLss;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewEventsLssRepository extends JpaRepository<NewEventsLss, Long> {
}
