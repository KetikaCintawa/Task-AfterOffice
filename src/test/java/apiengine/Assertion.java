package apiengine;

import com.apiautomation.model.ResponseItem;
import com.apiautomation.model.request.RequestItem;
import org.testng.Assert;

public class Assertion {

    public void assertAddObject(ResponseItem responseItem, RequestItem requestItem) {
        Assert.assertNotNull(responseItem, "Response item is null");
        Assert.assertEquals(responseItem.name, requestItem.name, "Product name mismatch");
        Assert.assertNotNull(responseItem.createdAt, "Created timestamp is null");
        Assert.assertNotNull(responseItem.id, "Product ID is null");

        if (responseItem.data != null && requestItem.data != null) {
            if (requestItem.data.year > 0) {
                Assert.assertEquals(responseItem.data.year, requestItem.data.year, "Year mismatch");
            }
            if (requestItem.data.price > 0) {
                Assert.assertEquals(responseItem.data.price, requestItem.data.price, "Price mismatch");
            }
            if (requestItem.data.cpuModel != null) {
                Assert.assertEquals(responseItem.data.cpuModel, requestItem.data.cpuModel, "CPU model mismatch");
            }
            if (requestItem.data.hardDiskSize != null) {
                Assert.assertEquals(responseItem.data.hardDiskSize, requestItem.data.hardDiskSize, "Hard disk size mismatch");
            }
        }
    }

    public void assertUpdateObject(ResponseItem responseItem, RequestItem requestItem) {
        Assert.assertNotNull(responseItem, "Updated item response is null");
        Assert.assertEquals(responseItem.name, requestItem.name, "Updated name doesn't match request");
        Assert.assertNotNull(responseItem.updatedAt, "Updated timestamp is null");

        if (responseItem.data != null && requestItem.data != null) {
            if (requestItem.data.year > 0) {
                Assert.assertEquals(responseItem.data.year, requestItem.data.year, "Updated year doesn't match request");
            }
            if (requestItem.data.price > 0) {
                Assert.assertEquals(responseItem.data.price, requestItem.data.price, "Updated price doesn't match request");
            }
            if (requestItem.data.cpuModel != null) {
                Assert.assertEquals(responseItem.data.cpuModel, requestItem.data.cpuModel, "Updated CPU model doesn't match request");
            }
            if (requestItem.data.hardDiskSize != null) {
                Assert.assertEquals(responseItem.data.hardDiskSize, requestItem.data.hardDiskSize, "Updated hard disk size doesn't match request");
            }
        }
    }
}