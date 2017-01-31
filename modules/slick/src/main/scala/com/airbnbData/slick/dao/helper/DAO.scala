package com.airbnbData.slick.dao.helper
//
///**
//  * Created by Lance on 1/29/17.
//  */
//import slick.lifted.{AppliedCompiledFunction, ForeignKeyQuery}
//import slick.ast.BaseTypedType
//import slick.relational.RelationalProfile
//import scala.concurrent.ExecutionContext
//
//
//trait DAO[T <: DTO[ID], ID] { self: Profile =>
//  import profile.api._
//
//  type TableType <: Keyed[ID] with RelationalProfile#Table[T]
//  def pkType: BaseTypedType[ID]
//  implicit lazy val _pkType: BaseTypedType[ID] = pkType
//  def tableQuery: TableQuery[TableType]
//  type F = AppliedCompiledFunction[_, Query[TableType, T, Seq], Seq[T]]
//
//  /**
//    * Find all.
//    * @return
//    */
//  def findAll(): DBIO[Seq[T]] = {
//    tableQuery.result
//  }
//
//  /**
//    * Find all
//    * @return
//    */
//  def streamingFindAll: StreamingDBIO[Seq[T], T] = tableQuery.result
//
//  /**
//    * Finds a given entity by its primary key.
//    */
//  def findById(id: ID): DBIO[Option[T]] = findOneCompiled(id).result.headOption
//
//  /**
//    * Insert a row.
//    * @param entity The entity to be inserted.
//    * @param ec Execution context.
//    * @return 1 indicating the insertion is successful.
//    */
//  def insert(entity: T)(implicit ec: ExecutionContext): DBIO[Int] =
//    (tableQueryCompiled += entity)
//
//  /**
//    * Insert a row.
//    * @param entity The entity to be inserted.
//    * @param ec Execution context.
//    * @return Return the id of the row.
//    */
//  def insertAndReturnId(entity: T)(implicit ec: ExecutionContext): DBIO[ID] =
//    // TODO: Can this be compiled?
//    tableQuery returning tableQuery.map(_.id) += entity
//
//  /**
//    * Insert a row.
//    * @param entity The entity to be inserted.
//    * @param ec Execution context.
//    * @return Return the entire row.
//    */
//  def insertAndReturnRow(entity: T)(implicit ec: ExecutionContext): DBIO[T] =
//    tableQuery returning tableQuery += entity
//
//  /**
//    * Performs a batch insert of the entities that are passed in
//    * as an argument. The result will be the number of created
//    * entities in case of a successful batch insert execution
//    * (if the row count is provided by the underlying database
//    * or driver. If not, then `None` will be returned as the
//    * result of a successful batch insert operation).
//    */
//  def batchInsert(entities: Seq[T]): DBIO[Option[Int]] =
//    batchPersister(entities)
//
//  /**
//    * Batch persister
//    */
//  protected val batchPersister: Seq[T] => DBIO[Option[Int]] =
//    getBatchPersister(e => e)
//
//  /**
//    * Builds a batch persister
//    */
//  protected def getBatchPersister(transformer: T => T): Seq[T] => DBIO[Option[Int]] =
//    (entities: Seq[T]) => tableQueryCompiled ++= entities.map(transformer)
//
//  /**
//    * Updates a given entity in the database.
//    *
//    * If the entity is not yet persisted in the database then
//    * this operation will result in an exception being thrown.
//    *
//    * Returns the same entity instance that was passed in as
//    * an argument.
//    */
//  def update(entity: T)(implicit ec: ExecutionContext): DBIO[T] =
//    updater(entity, updateFinder(entity), ec)
//
//  /**
//    * Update validator
//    */
//  protected def updateValidator(previous: T, next: T): Int => T = _ => next
//
//  /**
//    * Update finder
//    */
//  protected def updateFinder(entity: T): F =
//    findOneCompiled(entity.id)
//
//  /**
//    * Updater
//    */
//  protected val updater: (T, F, ExecutionContext) => DBIO[T] =
//    getUpdater(e => e)
//
//  /**
//    * Builds an updater
//    */
//  protected def getUpdater(transformer: T => T): (T, F, ExecutionContext) => DBIO[T] =
//    (entity: T, finder: F, ec: ExecutionContext) => {
//      val transformed = transformer(entity)
//      finder.update(transformed).map(updateValidator(entity, transformed))(ec)
//    }
//
//  /**
//    * Deletes a given entity from the database.
//    *
//    * If the entity is not yet persisted in the database then
//    * this operation will result in an exception being thrown.
//    */
//  def deleteBy(id: ID): DBIO[Int] = {
//    findOneCompiled(id).delete
//  }
//
//  /**
//    * Delete all entities in the table.
//    * @return
//    */
//  def deleteAll(): DBIO[Int] =
//    tableQuery.delete
//
//  /**
//    * Counts all entities.
//    */
//  def count(): DBIO[Int] = {
//    countCompiled.result
//  }
//
//  /**
//    * Executes the given unit of work in a single transaction.
//    */
//  def executeTransactionally[R](work: DBIO[R]): DBIO[R] = {
//    work.transactionally
//  }
//
//  lazy protected val tableQueryCompiled = Compiled(tableQuery)
//  lazy protected val findOneCompiled = Compiled((id: Rep[ID]) => tableQuery.filter(_.id === id))
//  lazy protected val saveCompiled = tableQuery returning tableQuery.map(_.id)
//  lazy private val countCompiled = Compiled(tableQuery.map(_.id).length)
//
//}


import slick.jdbc.JdbcProfile
import slick.ast.BaseTypedType
import slick.lifted.AppliedCompiledFunction
import slick.relational.RelationalProfile
import scala.concurrent.ExecutionContext

abstract class DAO[TableType <: Keyed[ID] with RelationalProfile#Table[DTOType], DTOType <: DTO[ID], ID: BaseTypedType](protected val profile: JdbcProfile) {
  import profile.api._

  type F = AppliedCompiledFunction[_, Query[TableType, DTOType, Seq], Seq[DTOType]]

  def tableQuery: TableQuery[TableType]

  def getId(row: TableType): Rep[ID] = row.id

  def filterById(id: ID) = tableQuery filter (getId(_) === id)


  /**
    * Find all.
    * @return
    */
  def getAll: DBIO[Seq[DTOType]] = {
    tableQuery.result
  }

  def streamingGetAll: StreamingDBIO[Seq[DTOType], DTOType] = tableQuery.result

  /**
    * Finds a given entity by its primary key.
    */
  def findById(id: ID): DBIO[Option[DTOType]] = findOneCompiled(id).result.headOption

  /**
    * Insert a row.
    * @param entity The entity to be inserted.
    * @param ec Execution context.
    * @return 1 indicating the insertion is successful.
    */
  def insert(entity: DTOType)(implicit ec: ExecutionContext): DBIO[Int] =
    (tableQueryCompiled += entity)

  /**
    * Insert a row.
    * @param entity The entity to be inserted.
    * @param ec Execution context.
    * @return Return the id of the row.
    */
  def insertAndReturnId(entity: DTOType)(implicit ec: ExecutionContext): DBIO[ID] =
  // TODO: Can this be compiled?
    tableQuery returning tableQuery.map(_.id) += entity

  /**
    * Insert a row.
    * @param entity The entity to be inserted.
    * @param ec Execution context.
    * @return Return the entire row.
    */
  def insertAndReturnRow(entity: DTOType)(implicit ec: ExecutionContext): DBIO[DTOType] =
    tableQuery returning tableQuery += entity

  /**
    * Performs a batch insert of the entities that are passed in
    * as an argument. The result will be the number of created
    * entities in case of a successful batch insert execution
    * (if the row count is provided by the underlying database
    * or driver. If not, then `None` will be returned as the
    * result of a successful batch insert operation).
    */
  def batchInsert(entities: Seq[DTOType]): DBIO[Option[Int]] =
    batchPersister(entities)

  /**
    * Batch persister
    */
  protected val batchPersister: Seq[DTOType] => DBIO[Option[Int]] =
    getBatchPersister(e => e)

  /**
    * Builds a batch persister
    */
  protected def getBatchPersister(transformer: DTOType => DTOType): Seq[DTOType] => DBIO[Option[Int]] =
    (entities: Seq[DTOType]) => tableQueryCompiled ++= entities.map(transformer)

  /**
    * Updates a given entity in the database.
    *
    * If the entity is not yet persisted in the database then
    * this operation will result in an exception being thrown.
    *
    * Returns the same entity instance that was passed in as
    * an argument.
    */
  def update(entity: DTOType)(implicit ec: ExecutionContext): DBIO[DTOType] =
    updater(entity, updateFinder(entity), ec)

  /**
    * Update validator
    */
  protected def updateValidator(previous: DTOType, next: DTOType): Int => DTOType = _ => next

  /**
    * Update finder
    */
  protected def updateFinder(entity: DTOType): F =
    findOneCompiled(entity.id)

  /**
    * Updater
    */
  protected val updater: (DTOType, F, ExecutionContext) => DBIO[DTOType] =
    getUpdater(e => e)

  /**
    * Builds an updater
    */
  protected def getUpdater(transformer: DTOType => DTOType): (DTOType, F, ExecutionContext) => DBIO[DTOType] =
    (entity: DTOType, finder: F, ec: ExecutionContext) => {
      val transformed = transformer(entity)
      finder.update(transformed).map(updateValidator(entity, transformed))(ec)
    }

  /**
    * Deletes a given entity from the database.
    *
    * If the entity is not yet persisted in the database then
    * this operation will result in an exception being thrown.
    */
  def deleteById(id: ID): DBIO[Int] = {
    findOneCompiled(id).delete
  }

  /**
    * Delete all entities in the table.
    * @return
    */
  def deleteAll(): DBIO[Int] =
    tableQuery.delete

  /**
    * Counts all entities.
    */
  def count(): DBIO[Int] = {
    countCompiled.result
  }

  /**
    * Executes the given unit of work in a single transaction.
    */
  def executeTransactionally[R](work: DBIO[R]): DBIO[R] = {
    work.transactionally
  }

  lazy protected val tableQueryCompiled = Compiled(tableQuery)
  lazy protected val findOneCompiled = Compiled((id: Rep[ID]) => tableQuery.filter(_.id === id))
  lazy protected val saveCompiled = tableQuery returning tableQuery.map(_.id)
  lazy private val countCompiled = Compiled(tableQuery.map(_.id).length)
}