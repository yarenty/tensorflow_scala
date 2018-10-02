/* Copyright 2017-18, Emmanouil Antonios Platanios. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.platanios.tensorflow.api.ops.lookup

import org.platanios.tensorflow.api.core.Graph
import org.platanios.tensorflow.api.ops.{Op, Output, UntypedOp}

/** Lookup table initializer that uses the provided tensors (containing keys and corresponding values) for initializing
  * a lookup table.
  *
  * @param  keys   Tensor containing the table keys.
  * @param  values Tensor containing the table values.
  *
  * @author Emmanouil Antonios Platanios
  */
class LookupTableTensorInitializer[K, +V] protected (
    val keys: Output[K],
    val values: Output[V]
) extends LookupTableInitializer(keys.dataType, values.dataType) {
  /** Creates and returns an op that initializes the provided table.
    *
    * @param  table Table to initialize.
    * @return Created initialization op for `table`.
    */
  override def initialize[VV >: V](
      table: InitializableLookupTable[K, VV],
      name: String = "Initialize"
  ): UntypedOp = {
    Op.nameScope(name) {
      val initializationOp = Op.Builder[(Output[Long], Output[K], Output[V]), Unit](
        opType = "InitializeTableV2",
        name = name,
        input = (table.handle, keys, values)
      ).build()
      Op.currentGraph.addToCollection(initializationOp, Graph.Keys.TABLE_INITIALIZERS)
      initializationOp.asUntyped
    }
  }
}

object LookupTableTensorInitializer {
  def apply[K, V](
      keys: Output[K],
      values: Output[V]
  ): LookupTableTensorInitializer[K, V] = {
    new LookupTableTensorInitializer(keys, values)
  }
}
