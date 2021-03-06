// Copyright (C) 2019 Google LLC
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

package com.quantumsoft.qupathcloud.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.Objects;

public class DicomAttribute<T>{
    private String vr;
    @JsonProperty
    private T[] Value;

    public String getVr() {
        return vr;
    }

    public void setVr(String vr) {
        this.vr = vr;
    }

    public void setValue(T[] Value) {
        this.Value = Value;
    }

    @JsonIgnore
    public T getValue1() {
        return Value[0];
    }

    @JsonIgnore
    public T getValue2() {
        return Value[1];
    }

    @JsonIgnore
    public T[] getValue() {
        return Value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DicomAttribute<?> that = (DicomAttribute<?>) o;
        return Objects.equals(vr, that.vr) &&
                Arrays.equals(Value, that.Value);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(vr);
        result = 31 * result + Arrays.hashCode(Value);
        return result;
    }
}
