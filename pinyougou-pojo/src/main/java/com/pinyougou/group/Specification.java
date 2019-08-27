package com.pinyougou.group;

import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbSpecificationOption;

import java.io.Serializable;
import java.util.List;


public class Specification implements Serializable {
    private TbSpecification spec;
    private List<TbSpecificationOption> specificationOptionList;

    public TbSpecification getSpec() {
        return spec;
    }

    public void setSpec(TbSpecification spec) {
        this.spec = spec;
    }

    public List<TbSpecificationOption> getSpecificationOptionList() {
        return specificationOptionList;
    }

    public void setSpecificationOptionList(List<TbSpecificationOption> specificationOptionList) {
        this.specificationOptionList = specificationOptionList;
    }

    @Override
    public String toString() {
        return "Specification{" +
                "spec=" + spec +
                ", specificationOptionList=" + specificationOptionList +
                '}';
    }
}
