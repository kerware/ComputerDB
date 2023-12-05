import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './computer.reducer';

export const ComputerDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const computerEntity = useAppSelector(state => state.computer.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="computerDetailsHeading">
          <Translate contentKey="computerDbApp.computer.detail.title">Computer</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{computerEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="computerDbApp.computer.name">Name</Translate>
            </span>
          </dt>
          <dd>{computerEntity.name}</dd>
          <dt>
            <span id="introduced">
              <Translate contentKey="computerDbApp.computer.introduced">Introduced</Translate>
            </span>
          </dt>
          <dd>
            {computerEntity.introduced ? <TextFormat value={computerEntity.introduced} type="date" format={APP_LOCAL_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="removed">
              <Translate contentKey="computerDbApp.computer.removed">Removed</Translate>
            </span>
          </dt>
          <dd>
            {computerEntity.removed ? <TextFormat value={computerEntity.removed} type="date" format={APP_LOCAL_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="hardware">
              <Translate contentKey="computerDbApp.computer.hardware">Hardware</Translate>
            </span>
          </dt>
          <dd>{computerEntity.hardware}</dd>
          <dt>
            <span id="software">
              <Translate contentKey="computerDbApp.computer.software">Software</Translate>
            </span>
          </dt>
          <dd>{computerEntity.software}</dd>
          <dt>
            <Translate contentKey="computerDbApp.computer.company">Company</Translate>
          </dt>
          <dd>{computerEntity.company ? computerEntity.company.name : ''}</dd>
        </dl>
        <Button tag={Link} to="/computer" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/computer/${computerEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ComputerDetail;
