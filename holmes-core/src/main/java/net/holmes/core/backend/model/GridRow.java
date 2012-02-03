/**
* Copyright (c) 2012 Cedric Cheneau
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
* THE SOFTWARE.
*/
package net.holmes.core.backend.model;

import java.io.Serializable;
import java.util.Collection;

/**
 * The Class GridRow.
 */
public class GridRow implements Serializable
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -7055943024207235434L;

    /** The id. */
    private String id;

    /** The cell. */
    private Collection<String> cell;

    /**
     * Instantiates a new grid row.
     */
    public GridRow()
    {
    }

    /**
     * Instantiates a new grid row.
     *
     * @param id the id
     * @param cell the cell
     */
    public GridRow(String id, Collection<String> cell)
    {
        this.id = id;
        this.cell = cell;
    }

    /**
      * Gets the id.
      *
      * @return the id
      */
    public String getId()
    {
        return id;
    }

    /**
     * Sets the id.
     *
     * @param id the new id
     */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * Gets the cell.
     *
     * @return the cell
     */
    public Collection<String> getCell()
    {
        return cell;
    }

    /**
     * Sets the cell.
     *
     * @param cell the new cell
     */
    public void setCell(Collection<String> cell)
    {
        this.cell = cell;
    }
}
