import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

import static ticker.worker.Quotes.BID_TREE;
import static ticker.worker.Quotes.ASK_TREE;

@Slf4j
@Service
@RequiredArgsConstructor
public class TickService {

    void filledOrder(TickerDto dto) {
        switch (dto.getDirection().toUpperCase()) {
            case "BID":
                this.updateTick(BID_TREE, dto, "filled");
                break;
            case "ASK":
                this.updateTick(ASK_TREE, dto, "filled");
                break;
        }
        this.setLastPrice(dto.getPrice());
    }

    public void executeBidOrder(TickerDto dto) {
        log.info("execute : price : {}, direction : {}, quantity : {} ", dto.getPrice(), dto.getDirection(), dto.getQuantity());
        this.updateTick(BID_TREE, dto, "executed");
    }

    public void executeAskOrder(TickerDto dto) {
        log.info("execute : price : {}, direction : {}, quantity : {} ", dto.getPrice(), dto.getDirection(), dto.getQuantity());
        this.updateTick(ASK_TREE, dto, "executed");
    }

    private void updateTick(Map<Double, Long> map, TickerDto dto, String type) {

        if (!map.containsKey(dto.getPrice())) {
            log.info("add {}, {}, {}, {}", dto.getDirection(), dto.getQuantity(), dto.getPrice(), dto.getQuantity());
            map.put(dto.getPrice(), dto.getQuantity());
        } else {
            long resultQuantity;
            log.info("update {}, {}, {}, {}", dto.getDirection(), dto.getQuantity(), dto.getPrice(), dto.getQuantity());
            if (type.equals("executed")) {
                resultQuantity = map.get(dto.getPrice()) + dto.getQuantity();
            } else {
                resultQuantity = map.get(dto.getPrice()) - dto.getQuantity();
            }

            map.put(dto.getPrice(), resultQuantity);
            if (resultQuantity == 0) {
                map.remove(dto.getPrice());
            }
        }
    }
}