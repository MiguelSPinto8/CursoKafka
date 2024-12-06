package or.br.mineradora.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import or.br.mineradora.client.CurrencyPriceClient;
import or.br.mineradora.dto.CurrencyPriceDTO;
import or.br.mineradora.dto.QuotationDTO;
import or.br.mineradora.entity.QuotationEntity;
import or.br.mineradora.message.KafkaEvents;
import or.br.mineradora.repository.QuotationRepository;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@ApplicationScoped
public class QuotationService {

    @Inject
    @RestClient
    CurrencyPriceClient currencyPriceClient;

    @Inject
    QuotationRepository quotationRepository;

    @Inject
    KafkaEvents kafkaEvents;

    public void getCurrencyPrice(){

        CurrencyPriceDTO currencyPriceInfo = currencyPriceClient.getPriceByPair("USD-BRL");

        if(updateCurrentInfoPrice(currencyPriceInfo)){
            kafkaEvents.sendNewKafkaEvent(QuotationDTO
                    .builder()
                    .currencyPrice(new BigDecimal(currencyPriceInfo.getUSDBRL().getBid()))
                    .date(new Date())
                    .build());
        }
    }

    private boolean updateCurrentInfoPrice(CurrencyPriceDTO currencyPriceInfo) {

        BigDecimal currentPrice = new BigDecimal(currencyPriceInfo.getUSDBRL().getBid());
        boolean updatePrice = false;

        List<QuotationEntity> quotationList = quotationRepository.findAll().list();



        if(quotationList.isEmpty()){

            saveQuotation(currencyPriceInfo);
            updatePrice = true;

        }else {

            QuotationEntity lastDollarPrice = quotationList
                    .get(quotationList.size()-1);


            if(currentPrice.floatValue() > lastDollarPrice.getCurrencyPrice().floatValue()){
                updatePrice = true;
                saveQuotation(currencyPriceInfo);
            }
        }

        return updatePrice;

    }

    private void saveQuotation(CurrencyPriceDTO currencyInfo){
        QuotationEntity quotation = new QuotationEntity();

        quotation.setDate(new Date());
        quotation.setCurrencyPrice(new BigDecimal(currencyInfo.getUSDBRL().getBid()));
        quotation.setPctChange(currencyInfo.getUSDBRL().getPctChange());
        quotation.setPair("USD-BRL");

        quotationRepository.persist(quotation);
    }
}
