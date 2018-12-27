import com.shyl.msc.count.service.IHospitalProductCService;
import com.shyl.msc.count.service.IOrderCService;
import com.shyl.msc.count.service.IOrderDetailCService;
import com.shyl.msc.count.service.IVendorProductCService;
import com.shyl.sys.entity.AttributeItem;
import com.shyl.sys.service.IAttributeItemService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class TestTask {
    /*@Resource
    private IOrderCService orderCService;
    @Resource
    private IOrderDetailCService orderDetailCService;
    @Resource
    private IHospitalProductCService hospitalProductCService;
    @Resource
    private IVendorProductCService vendorProductCService;
    @Resource
    private IAttributeItemService attributeItemService;*/

    @Test
    public void countTaskJob() {
        /*System.out.println("开始执行-------------countTaskJob");
        long begin = System.currentTimeMillis();
        List<AttributeItem> attributeItems = attributeItemService.listByAttributeNo("", "platform_no");
        for (AttributeItem attributeItem : attributeItems) {
            orderCService.pass(attributeItem.getField1());
            orderDetailCService.pass(attributeItem.getField1());

            hospitalProductCService.pass(attributeItem.getField1());
            vendorProductCService.pass(attributeItem.getField1());
        }
        long end = System.currentTimeMillis() - begin;
        System.out.println("完成执行-------------countTaskJob");
        System.out.println("耗时：" + end + "毫秒");*/
        System.out.println(444);
    }
}
