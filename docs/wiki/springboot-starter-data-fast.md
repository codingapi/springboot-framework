springboot-starter-data-fast

基于JPA的快速API能力服务

## FastRepository 的使用教程

继承FastRepository接口，实现自定义的接口，即可使用FastRepository的能力
```java


import com.codingapi.springboot.fast.entity.Demo;
import com.codingapi.springboot.fast.query.FastRepository;

public interface DemoRepository extends FastRepository<Demo,Integer> {

}


```
动态FastRepository的能力展示

```

    // 重写findAll，通过Example查询 
    @Test
    void findAll() {
            demoRepository.deleteAll();
            Demo demo1 = new Demo();
            demo1.setName("123");
            demoRepository.save(demo1);

            Demo demo2 = new Demo();
            demo2.setName("456");
            demoRepository.save(demo2);

            PageRequest request = new PageRequest();
            request.setCurrent(1);
            request.setPageSize(10);
            request.addFilter("name", "123");

            Page<Demo> page = demoRepository.findAll(request);
            assertEquals(1, page.getTotalElements());
        }


    // pageRequest 自定义条件查询        
    @Test
    void pageRequest1() {
            demoRepository.deleteAll();
            Demo demo1 = new Demo();
            demo1.setName("123");
            demoRepository.save(demo1);

            Demo demo2 = new Demo();
            demo2.setName("456");
            demoRepository.save(demo2);

            PageRequest request = new PageRequest();
            request.setCurrent(1);
            request.setPageSize(10);
            request.addFilter("name", PageRequest.FilterRelation.LIKE, "%2%");
            //sql: select demo0_.id as id1_0_, demo0_.name as name2_0_, demo0_.sort as sort3_0_ from t_demo demo0_ where demo0_.name like ? limit ?

            Page<Demo> page = demoRepository.pageRequest(request);
            assertEquals(1, page.getTotalElements());
    }
    
    // pageRequest 自定义条件查询      
    @Test
    void pageRequest2() {
            demoRepository.deleteAll();
            Demo demo1 = new Demo();
            demo1.setName("123");
            demoRepository.save(demo1);

            Demo demo2 = new Demo();
            demo2.setName("456");
            demoRepository.save(demo2);

            PageRequest request = new PageRequest();
            request.setCurrent(1);
            request.setPageSize(10);
            request.orFilters(Filter.as("name","123"),Filter.as("name","456"));
            //sql: select demo0_.id as id1_0_, demo0_.name as name2_0_, demo0_.sort as sort3_0_ from t_demo demo0_ where demo0_.name=? or demo0_.name=? limit ?

            Page<Demo> page = demoRepository.pageRequest(request);
            assertEquals(1, page.getTotalElements());
    }


    // 动态sql的List查询    
    @Test
    void dynamicListQuery() {
            demoRepository.deleteAll();
            Demo demo1 = new Demo();
            demo1.setName("123");
            demoRepository.save(demo1);

            Demo demo2 = new Demo();
            demo2.setName("456");
            demoRepository.save(demo2);

            List<Demo> list = demoRepository.dynamicListQuery("from Demo where name = ?1", "123");
            assertEquals(1, list.size());
        }


    // 动态sql的分页查询        
    @Test
    void dynamicPageQuery() {
            demoRepository.deleteAll();
            Demo demo1 = new Demo();
            demo1.setName("123");
            demoRepository.save(demo1);

            Demo demo2 = new Demo();
            demo2.setName("456");
            demoRepository.save(demo2);

            Page<Demo> page = demoRepository.dynamicPageQuery("from Demo where name = ?1", PageRequest.of(1, 2), "123");
            assertEquals(1, page.getTotalElements());
        }

    // 增加排序查询
    @Test
    void sortQuery() {
            demoRepository.deleteAll();
            Demo demo1 = new Demo();
            demo1.setName("123");
            demoRepository.save(demo1);

            Demo demo2 = new Demo();
            demo2.setName("456");
            demoRepository.save(demo2);

            PageRequest request = new PageRequest();
            request.setCurrent(1);
            request.setPageSize(10);

            request.addSort(Sort.by("id").descending());
            Page<Demo> page = demoRepository.findAll(request);
            assertEquals(page.getContent().get(0).getName(), "456");
            assertEquals(2, page.getTotalElements());
        }

```

## ScriptMapping 教程

通过动态添加mvc mapping实现查询功能.


```
ScriptMapping scriptMapping = new ScriptMapping({mapinggUrl}, {mapinggMethod}, {mappingGrovvry});
scriptMappingRegister.addMapping(scriptMapping);

```
mapinggUrl 是mvc接口的地址    
mapinggMethod 是mvc接口的请求方式  
mappingGrovvry 是执行的查询脚本  

脚本实例代码： 
* 动态分页查询
```
// 获取name的请求参数
var name = $request.getParameter("name","");
var pageNumber = $request.getParameter("pageNumber",0);
var pageSize = $request.getParameter("pageSize",10);
// 创建分页对象
var pageRequest = $request.pageRequest(pageNumber,pageSize);
// 动态组织sql
var sql = "select * from api_mapping where 1 =1 ";
var countSql = "select count(1) from api_mapping where 1 =1 ";
// 动态组织参数
var params = [];
if(!"".equals(name)){
   sql += " and name = ? ";
   countSql += " and name = ? ";
   params.push(name);
}
sql += " limit ?,?";
// 添加分页参数
params.add(pageRequest.getOffset());
params.add(pageRequest.getPageSize());
// 执行分页查询
return $jdbc.queryForPage(sql,countSql,pageRequest,params.toArray());
```
* 动态条件查询
```
// 获取name的请求参数
var name = $request.getParameter("name","");
// 动态组织sql
String sql = "select * from api_mapping where 1=1 ";
// 动态组织参数
var params = [];
if(!"".equals(name)){
    sql += " and name = ? ";
    params.add(name);
}
// 执行查询
return $jdbc.queryForList(sql,params.toArray());
```

脚本语法介绍：  
* $request
```
// 获取参数name的值，如果参数不存在，则返回默认值
var name = $request.getParameter("name","");
// 获取分页对象
var pageRequest = $request.pageRequest(0,10);
// 获取分页对象的页码
var pageNumber = pageRequest.getPageNumber();
// 获取分页对象的每页记录数
var pageSize = pageRequest.getPageSize();
// 获取分页对象的偏移量
var offset = pageRequest.getOffset();
```
* $jdbc
```
// 查询jdbcSQL $jdbc.queryForList({sql},{params})

// 查询无条件的数据
var res = $jdbc.queryForList("select * from api_mapping");
// 查询有条件的数据
var res = $jdbc.queryForList("select * from api_mapping where name = ?",name);
// 查询多条件的数据
var res = $jdbc.queryForList("select * from api_mapping where name = ? and url = ?",name,url);

// 分页查询 $jdbc.queryForPage({sql},{countSql},{pageRequest},{params})
var res = $jdbc.queryForPage("select * from api_mapping where name = ? and url = ?",
"select count(1) from api_mapping where name = ? and url = ?",pageRequest,params.toArray());
```
* $jpa
```
// 查询jpa $jpa.listQuery({clazz},{sql},{params})

// 查询无条件的数据
var res = $jpa.listQuery(com.example.entity.NodeEntity.class,"from NodeEntity");
// 查询有条件的数据
var res = $jpa.listQuery(com.example.entity.NodeEntity.class,"from NodeEntity where name = ?",name);
```