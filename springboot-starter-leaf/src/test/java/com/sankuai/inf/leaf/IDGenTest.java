package com.sankuai.inf.leaf;

import com.sankuai.inf.leaf.common.Result;
import com.sankuai.inf.leaf.segment.SegmentIDGenImpl;
import com.sankuai.inf.leaf.segment.dao.IDAllocDao;
import com.sankuai.inf.leaf.segment.dao.impl.IDAllocDaoImpl;
import com.sankuai.inf.leaf.segment.model.LeafAlloc;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
class IDGenTest {

    private IDAllocDao allocDao;
    private IDGen idGen;

    @BeforeEach
    public void beforeInitIdGen() {
        //setting jdbc url to h2 memory
        allocDao = new IDAllocDaoImpl("jdbc:h2:mem:leaf;DB_CLOSE_DELAY=-1");

        //add leaf-segment-test key
        LeafAlloc alloc = new LeafAlloc();
        alloc.setMaxId(0);
        alloc.setStep(100);
        alloc.setKey("leaf-segment-test");
        allocDao.save(alloc);

        //init id gen
        idGen = new SegmentIDGenImpl(allocDao);
        idGen.init();
    }


    @Test
    public void testGetId() {
        for (int i = 0; i < 100; ++i) {
            Result r = idGen.get("leaf-segment-test");
            assertEquals(r.getId(), (i), "id gen value error.");
            log.info("res:{}",r);
        }
    }

}