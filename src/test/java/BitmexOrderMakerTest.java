import bots.algoritms.ordermakers.BitmexOrderMaker;
import bots.algoritms.ordermakers.OrderMaker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BitmexOrderMakerTest{

    private OrderMaker orderMaker;
    @BeforeEach
    public void init(){
        orderMaker = new BitmexOrderMaker("9z27ZL8GIE_Z36YQn2wOEWOk", "u-uyAKmPIvoZ5FySeotto6389b0exVHF4UGzPY6jY7i_9t3A");
        OrderMaker orderMakerSpy = Mockito.spy(orderMaker);
    }

    @Test
    public void accountKeysIsValid() {

    }



}
