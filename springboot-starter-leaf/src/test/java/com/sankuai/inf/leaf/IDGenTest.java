package com.sankuai.inf.leaf;

import com.sankuai.inf.leaf.common.Result;
import com.sankuai.inf.leaf.segment.SegmentIDGenImpl;
import com.sankuai.inf.leaf.segment.dao.IDAllocDao;
import com.sankuai.inf.leaf.segment.dao.impl.IDAllocDaoImpl;
import com.sankuai.inf.leaf.segment.model.LeafAlloc;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IDGenTest {


    private IDAllocDao allocDao;
    private IDGen idGen;

    public IDGenTest() {
        allocDao = new IDAllocDaoImpl("jdbc:h2:mem:leaf;DB_CLOSE_DELAY=-1");
        LeafAlloc alloc = new LeafAlloc();
        alloc.setMaxId(1);
        alloc.setStep(1000);
        alloc.setKey("leaf-segment-test");

        allocDao.save(alloc);
        idGen = new SegmentIDGenImpl();
        ((SegmentIDGenImpl) idGen).setDao(allocDao);
        idGen.init();
    }


    @Test
    public void testGetId() {

        for (int i = 0; i < 100; ++i) {
            Result r = idGen.get("leaf-segment-test");
            System.out.println(r);
            assertEquals(r.getId(), (i+1), "id gen value error.");
        }
    }

}