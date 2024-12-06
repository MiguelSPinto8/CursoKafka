package or.br.mineradora.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import or.br.mineradora.entity.QuotationEntity;

@ApplicationScoped
public class QuotationRepository implements PanacheRepository<QuotationEntity> {
}
